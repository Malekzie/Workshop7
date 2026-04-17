# Wave 2 — Verification & Post-Deploy Checklist

Everything landed on `main` via PR #98. This doc covers:

1. [Environment variables you need to set](#1-environment-variables)
2. [Database migrations](#2-database-migrations)
3. [Stripe dashboard configuration](#3-stripe-dashboard-configuration)
4. [Local + CI verification steps per fix](#4-verification-steps-per-fix)
5. [Post-deploy smoke tests](#5-post-deploy-smoke-tests)
6. [Known gaps / Wave 3 candidates](#6-known-gaps--wave-3-candidates)

---

## 1. Environment variables

### 1a. Local development (`apps/backend/.env` and `apps/frontend/.env.local`)

Already required before Wave 2 (no change, listed for completeness):

| Var | Where | Purpose |
|---|---|---|
| `JWT_SECRET` | backend | Fail-fast if missing (A10) |
| `SENTRY_DSN` | backend | Empty is allowed; disables SDK |
| `PUBLIC_SENTRY_DSN` | frontend | Empty is allowed; disables SDK |
| `STRIPE_SECRET_KEY` | backend | Stripe API |
| `STRIPE_PUBLISHABLE_KEY` | backend | Served via `/api/v1/stripe/config` |
| `VALKEY_HOST`, `VALKEY_PORT`, `VALKEY_PASSWORD` | backend | Redis/Valkey |

**New as of Wave 2:**

| Var | Where | Required? | Notes |
|---|---|---|---|
| `STRIPE_WEBHOOK_SECRET` | backend | **Yes in prod** (`EnvValidator` fails fast) | Unset in dev falls back to unsigned parsing with a WARN log. Use `stripe listen` locally and copy its `whsec_...` value. |
| `SENTRY_RELEASE` | backend + frontend (SSR) | Optional | Empty → SDK omits the field. Set in CI to `${{ github.sha }}`. |
| `SENTRY_ENVIRONMENT` | backend + frontend (SSR) | Optional | Empty → SDK omits. Set to `production` / `preview` / `dev`. |
| `PUBLIC_SENTRY_RELEASE` | frontend (browser) | Optional | Same value as `SENTRY_RELEASE`; baked into the client bundle at build time. |
| `PUBLIC_SENTRY_ENVIRONMENT` | frontend (browser) | Optional | Same semantics as `SENTRY_ENVIRONMENT`. |

### 1b. GitHub repository secrets

Add under **Settings → Secrets and variables → Actions**:

| Secret | Already in use? | Wave 2 purpose |
|---|---|---|
| `DIGITALOCEAN_ACCESS_TOKEN` | Yes | Unchanged |
| `SENTRY_AUTH_TOKEN` | **No — add it** | Activates the sentry-maven-plugin profile (B7 backend source bundle upload) and lets `sentryVitePlugin` upload frontend sourcemaps. Without it, CI builds still succeed but source-map/source-bundle upload is silently skipped. Create at https://sentry.io/settings/account/api/auth-tokens/ with the `project:releases` scope. |

### 1c. DigitalOcean App Platform env vars (`.do/app.yaml`)

The Sentry runtime tags (`SENTRY_RELEASE`, `SENTRY_ENVIRONMENT`) are **not yet in the app spec** — only the build-time values in the workflow are wired. Three options, pick one:

- **Option A (cleanest):** add `SENTRY_ENVIRONMENT=production` as a static env var in `.do/app.yaml` under both backend and frontend services. Skip `SENTRY_RELEASE` at runtime — the backend source bundle + frontend sourcemaps are already keyed under the commit SHA by CI, so events tagged with `release: unknown` at runtime is a minor UX issue, not a correctness one. If you want the match, inject `SENTRY_RELEASE` via a separate workflow step that patches the app spec before deploy.
- **Option B:** set both vars in the DO dashboard under each component's environment panel. Faster to do by hand, but not reproducible from the repo.
- **Option C:** defer until Wave 3 — document the gap and live with `release: <unknown>` in Sentry until then.

Recommended: **Option A** with `SENTRY_RELEASE` skipped for now.

---

## 2. Database migrations

Flyway auto-applies `V48__stripe_processed_event.sql` on the first boot after merge:

```sql
CREATE TABLE stripe_processed_event (
    event_id      VARCHAR(255) PRIMARY KEY,
    processed_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
```

**Verify post-deploy:**

```sql
\d stripe_processed_event
SELECT COUNT(*) FROM flyway_schema_history WHERE version = '48' AND success = true;
```

If Flyway is disabled or the migration failed, the Stripe webhook will still compile but `claimEvent` will 500 every delivery.

---

## 3. Stripe dashboard configuration

Log into https://dashboard.stripe.com → **Developers → Webhooks**.

**For production:**

1. Add endpoint: `https://<your-prod-domain>/api/v1/stripe/webhook`
2. Events to send: at minimum `payment_intent.succeeded` (add more later if needed).
3. Copy the **Signing secret** (`whsec_...`) into the DO App Platform env var `STRIPE_WEBHOOK_SECRET`.
4. Redeploy the backend so the new secret is picked up.

**For local dev:**

```bash
stripe listen --forward-to localhost:8080/api/v1/stripe/webhook
```

The CLI prints a `whsec_...` on startup — paste it into `apps/backend/.env` as `STRIPE_WEBHOOK_SECRET`.

---

## 4. Verification steps per fix

Run the backend (`./mvnw spring-boot:run` in `apps/backend`) and the frontend (`npm run dev` in `apps/frontend`) before starting.

### B1 — Server-side recompute with role-gated discounts

| # | Action | Expected |
|---|---|---|
| B1.1 | As a **customer**, POST `/api/v1/orders/checkout` with `"manualDiscount": 9999` | Order created, `manualDiscount` silently ignored, `grandTotal` reflects only tier/special discounts |
| B1.2 | As a **customer**, POST checkout with `"pricingLocalDate": "2024-01-01"` (past date when a larger special existed) | Pricing uses today's date, not the supplied one |
| B1.3 | As an **admin/employee**, POST with `"manualDiscount": 5.00` | `tierDiscount` field shows 5.00, `grandTotal` is reduced |
| B1.4 | As an **admin/employee**, POST with a past `pricingLocalDate` | Past special is honored |

### B2 — Stripe webhook hardening

Get a real PaymentIntent id by checking out an order first. For amount tests, multiply `grandTotal` by 100 (cents).

| # | Action | Expected |
|---|---|---|
| B2.1 | `curl` the webhook with no `Stripe-Signature` header while `STRIPE_WEBHOOK_SECRET` is set | HTTP 400 "Invalid webhook", log `"signature header missing"`, order NOT paid |
| B2.2 | `curl` with bogus `Stripe-Signature: t=1,v1=deadbeef` | HTTP 400, log `"signature verification failed"` |
| B2.3 | `stripe trigger payment_intent.succeeded` → `stripe events resend <id>` | Second delivery returns 200 "duplicate"; order fulfilled once; rewards granted once; single confirmation email. Verify: `SELECT * FROM stripe_processed_event WHERE event_id = '<evt>'` shows one row. |
| B2.4 | `stripe trigger ... --override payment_intent:amount=100` for an order whose grandTotal isn't $1.00 | Log `"Amount mismatch for PaymentIntent ..."`, order remains `pending_payment` |
| B2.5 | Same but `--override payment_intent:currency=usd` | Log `"Currency mismatch ..."`, order remains pending |
| B2.6 | Happy path: real PI, matching amount, `cad` | Order → `paid`, email queued, rewards granted |

### B3 — Auth rate limiting

All from a single IP unless noted. Stop Redis to test fail-open.

| # | Action | Expected |
|---|---|---|
| B3.1 | 12× `POST /api/v1/auth/login` with bad creds | First 10 → 401, 11th and 12th → 429 with `Retry-After` header |
| B3.2 | 6× `POST /api/v1/auth/register` | 6th → 429 |
| B3.3 | 4× `POST /api/v1/auth/forgot-password` | 4th → 429 |
| B3.4 | 6× `POST /api/v1/auth/reset-password` (within 15 min) | 6th → 429 |
| B3.5 | 21× `GET /api/v1/auth/validate` | 21st → 429 |
| B3.6 | Repeat B3.1 with `-H "X-Forwarded-For: 9.9.9.9"` after exhausting your own IP | 9.9.9.9 counter starts fresh |
| B3.7 | Stop Redis, repeat B3.1 | Requests pass through, log `"Rate limit check failed; allowing request"` |

### B4 / B5 — CI hardening (review-only)

| # | Action | Expected |
|---|---|---|
| B4.1 | Open `.github/workflows/*.yml` | Every `uses:` line references a 40-char SHA with a trailing `# vN` comment |
| B5.1 | Push a commit to a fork, open a PR from the fork | "PR Preview" job is skipped (gated by `head.repo.full_name == github.repository`) |

### B6 — Sentry log redaction

Needs a real `SENTRY_DSN`. Trigger errors deliberately.

| # | Action | Expected |
|---|---|---|
| B6.1 | Log a message containing `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4In0.deadbeef` | Sentry event's `message` shows `... [redacted]` |
| B6.2 | Log a message containing `alice@example.com` | Sentry shows `... [redacted]` |
| B6.3 | Trigger an error after a request that sets `Authorization: Bearer xxx` | `Authorization` does not appear in Sentry breadcrumbs |
| B6.4 | Log a normal message like `"Order 42 failed"` | Message arrives unchanged |

### B7 — Sentry release + environment

| # | Action | Expected |
|---|---|---|
| B7.1 | Set `SENTRY_RELEASE=test-abc`, `SENTRY_ENVIRONMENT=staging`; restart backend; trigger an error | Sentry event shows matching `release` + `environment` |
| B7.2 | Set `PUBLIC_SENTRY_RELEASE=test-abc`, `PUBLIC_SENTRY_ENVIRONMENT=staging`; `npm run build && npm run preview`; trigger a client error | Sentry event shows matching tags |
| B7.3 | Open latest `Frontend CI` → Build step log | `sentryVitePlugin` reports sourcemap upload (if `SENTRY_AUTH_TOKEN` secret is set) |
| B7.4 | Open latest `Backend CI` → Build step log | `sentry-maven-plugin` reports source bundle upload (if token is set) |

---

## 5. Post-deploy smoke tests

Run these **once** after the first prod deploy that includes Wave 2:

1. Open `https://<prod-domain>/swagger-ui/index.html` — Swagger loads (CSP regression check; Wave 1 relaxed `script-src` for this).
2. Register a new test customer → confirmation email arrives.
3. Log in, add items, check out with Stripe test card `4242 4242 4242 4242`.
4. Observe the Stripe dashboard `Events` tab — the `payment_intent.succeeded` event shows a 200 response.
5. Re-send that event from the Stripe dashboard ("..." → Resend). Observe: 200 "duplicate", order stays `paid`, **no** duplicate email, **no** duplicate reward.
6. Query `SELECT COUNT(*) FROM stripe_processed_event;` — grows by one per unique event.
7. Hit `/api/v1/auth/login` with bad creds 11× in a minute from a single IP → 11th returns 429.
8. Trigger an error in the backend (e.g., submit a malformed order) → Sentry receives the event, tagged with `environment: production` (and `release: <sha>` if you went with Option A).
9. Confirm the Sentry event's stack frames resolve to source lines (validates B7 source bundle upload).

Any failure here → roll back, don't try to patch in place.

---

## 6. Known gaps / Wave 3 candidates

Wave 2 intentionally stopped short of the following. These are the next cut:

- **S1 / S2 / S5 / S6** — Sentry frontend hardening still has `sendDefaultPii:true`-era risks in auth-store user tagging from `localStorage` and the `/test-error` endpoint. Partially addressed in A14 but not fully.
- **`.do/app.yaml` encrypted-secrets blocker** — preview deploys fail because DO rejects encrypted env vars when creating a brand-new preview app. Needs the secrets re-encrypted against a DO key the workflow can reach, or moved out of the app spec entirely.
- **SENTRY_RELEASE at runtime** — see §1c above. Currently only the build-time upload is tagged with the SHA; the running container reports `release: <unknown>`.
- **Rate-limit monitoring** — the filter fails open on Redis outage, which is correct behavior but should emit a metric so an ops dashboard can alert on it. No metric today.
- **Remaining H/M findings** — H4 (CSRF on mutation endpoints), H8 (secrets in backend logs on OAuth failure), M1–M12 in `new-security-findings.md`.

Triage these before starting Wave 3 — some may already be obviated by other work.
