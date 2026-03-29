# Contributing to Peelin' Good

Welcome to the team! This guide covers everything you need to know about contributing code to the project.

---

## Table of Contents

- [Golden Rules](#golden-rules)
- [Development Setup](#development-setup)
- [Branching Strategy](#branching-strategy)
- [Making Changes](#making-changes)
- [Commit Messages](#commit-messages)
- [Pull Requests](#pull-requests)
- [Code Review](#code-review)
- [CI/CD Checks](#cicd-checks)
- [CodeScene Delta Analysis](#codescene-delta-analysis)
- [Merging](#merging)
- [Deployment](#deployment)
- [Database Migrations](#database-migrations)
- [Resolving Merge Conflicts](#resolving-merge-conflicts)
- [Troubleshooting](#troubleshooting)

---

## Golden Rules

1. **Never push directly to `main`.** The branch is protected — all changes go through pull requests.
2. **Never merge a PR with conflicts.** GitHub will block it. Resolve conflicts in your feature branch first.
3. **Never modify an existing Flyway migration.** Always create a new one.
4. **Run checks locally before pushing.** Don't rely on CI to catch things you can catch on your machine.
5. **Keep PRs small and focused.** One feature or fix per PR. Smaller PRs get reviewed faster and are less likely to cause conflicts.

---

## Development Setup

See the main [README](../README.md) for prerequisites and setup instructions.

**Quick start:**

```sh
# Clone the repo
git clone <repository-url>
cd Workshop7

# Backend
cd apps/backend
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows

# Frontend (in a separate terminal)
cd apps/frontend
npm install
npm run dev
```

For detailed setup, see:

- [Backend documentation](./backend.md)
- [Frontend documentation](./frontend.md)

---

## Branching Strategy

We use a simple **feature branch** workflow:

```
main (protected, production)
 └── feature/your-feature-name
 └── fix/bug-description
 └── chore/task-description
```

### Branch naming convention

Use a descriptive prefix followed by a short kebab-case name:

| Prefix | Use Case | Example |
| --- | --- | --- |
| `feature/` | New features | `feature/user-authentication` |
| `fix/` | Bug fixes | `fix/login-redirect-loop` |
| `chore/` | Maintenance, refactoring, docs | `chore/update-dependencies` |
| `hotfix/` | Urgent production fixes | `hotfix/crash-on-startup` |

---

## Making Changes

### 1. Create a feature branch

```sh
git checkout main
git pull origin main
git checkout -b feature/your-feature-name
```

### 2. Make your changes

Work on your feature, committing as you go. Before pushing, run the local checks:

**Frontend:**

```sh
cd apps/frontend
npm run format        # Auto-fix formatting
npm run lint          # Check linting + formatting
npm run check         # TypeScript / Svelte type checking
npm run build         # Make sure it builds
```

**Backend:**

```sh
cd apps/backend
./mvnw verify         # Compile + run all tests
```

### 3. Push and open a PR

```sh
git push -u origin feature/your-feature-name
```

Then open a pull request on GitHub targeting `main`.

---

## Commit Messages

Write clear, concise commit messages. Use the **imperative mood** (as if completing the sentence "This commit will..."):

**Good:**

- `Add user registration endpoint`
- `Fix navbar not rendering on mobile`
- `Update Tailwind to v4.1.18`

**Bad:**

- `added stuff`
- `fix`
- `WIP`
- `asdfasdf`

### Format

```
<type>: <short description>

<optional longer description>
```

Common types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`, `style`

**Examples:**

```
feat: add user registration endpoint

Adds POST /api/users/register with email/password validation.
Includes Flyway migration for the users table.
```

```
fix: prevent crash when session cookie is expired
```

---

## Pull Requests

### Opening a PR

1. Give your PR a **clear, descriptive title** (same conventions as commit messages).
2. Fill in the PR description with:
   - **What** the PR does
   - **Why** the change is needed
   - **How** to test it (if not obvious)
   - Any **screenshots** for UI changes
3. Assign yourself as the author.
4. Request reviewers from the team.

### PR size guidelines

| Size | Lines Changed | Review Time |
| --- | --- | --- |
| 🟢 Small | < 100 | Quick review |
| 🟡 Medium | 100–300 | Normal review |
| 🔴 Large | 300+ | Consider splitting |

If your PR is getting large, break it into smaller, independent PRs that can be reviewed and merged separately.

---

## Code Review

### As a reviewer

- Be constructive and specific. Suggest improvements, don't just point out problems.
- Approve if the code is good enough — it doesn't have to be perfect.
- Focus on:
  - Correctness — does it do what it's supposed to?
  - Readability — can you understand it without the author explaining?
  - Security — any potential vulnerabilities?
  - Performance — any obvious inefficiencies?

### As an author

- Respond to all review comments.
- Don't take feedback personally — we're all improving the code together.
- If you disagree with a suggestion, explain your reasoning.
- Push fixes as new commits (don't force-push during review so reviewers can see what changed).

---

## CI/CD Checks

When you open or update a PR, **GitHub Actions** automatically runs the relevant pipelines based on which files you changed:

### Backend changes (`apps/backend/**`)

| Step | What it does |
| --- | --- |
| JDK 21 setup | Installs Java with Maven caching |
| `./mvnw verify` | Compiles code and runs all tests |
| Docker build | Verifies the Docker image builds successfully |
| PR preview deploy | Deploys a preview to DigitalOcean |

### Frontend changes (`apps/frontend/**`)

| Step | What it does |
| --- | --- |
| Node 22 setup | Installs Node.js with npm caching |
| `npm ci` | Clean install of dependencies |
| `npm run lint` | Checks ESLint and Prettier rules |
| `npm run check` | Runs TypeScript / Svelte type checking |
| `npm run build` | Production build |
| Docker build | Verifies the Docker image builds successfully |
| PR preview deploy | Deploys a preview to DigitalOcean |

### What happens after CI runs

- ✅ **All checks pass** — a comment is posted on the PR with the **preview URL** so you and reviewers can test the changes live.
- ❌ **A check fails** — a comment is posted with a **link to the failed logs**. The PR cannot be merged until the issue is fixed.

> **Both pipelines are path-scoped.** If you only change frontend files, the backend pipeline won't run, and vice versa.

---

## CodeScene Delta Analysis

[CodeScene](https://codescene.com/) runs a **Delta Analysis** on every PR to evaluate the code health impact of your changes. It will comment directly on the PR with its findings.

### What CodeScene looks for

| Finding | What it means |
| --- | --- |
| **Code Health Decline** | Your changes increased complexity or decreased readability in a file |
| **Hotspot** | You're modifying a file that's already a complexity hotspot |
| **Growing function** | A function is getting too long or complex |
| **Code duplication** | Similar code patterns detected across files |
| **Coupling** | Files that are frequently changed together |

### How to respond

- **Green (no issues)** — you're good, no action needed.
- **Yellow (warnings)** — consider addressing the findings, but they won't block your PR.
- **Red (alerts)** — strongly consider refactoring. These indicate significant code health regressions.

CodeScene's feedback is there to help you write maintainable code. Take it as guidance, not gospel.

---

## Merging

### Requirements for merging

All of the following must be true before a PR can be merged:

1. ✅ All CI checks pass (GitHub Actions workflows)
2. ✅ No merge conflicts with `main`
3. ✅ At least one approving review (team convention)
4. ✅ All review conversations resolved

### Merge method

Use **"Squash and merge"** to keep the `main` branch history clean. This combines all your commits into a single commit with the PR title as the message.

### After merging

- GitHub Actions will automatically **deploy to production** on DigitalOcean.
- **Delete your feature branch** — GitHub can do this automatically if enabled, or do it manually:

```sh
git checkout main
git pull origin main
git branch -d feature/your-feature-name
```

---

## Deployment

Deployment is fully automated. You don't need to do anything manually.

| Event | What happens |
| --- | --- |
| **PR opened/updated** | Preview environment deployed → URL commented on PR |
| **PR merged to `main`** | Production deployment triggered automatically |

The flow for production:

```
Merge to main → GitHub Actions → Build & Test → Docker Image → Push to GHCR → Deploy to DigitalOcean
```

If a production deploy fails, check the **Actions** tab on GitHub for logs.

---

## Database Migrations

We use **Flyway** for database schema management. If your changes require database modifications:

### Creating a new migration

1. Check the highest existing version number in `apps/backend/src/main/resources/db/migration/`.
2. Create a new file with the next version number:

```
V{next_number}__{description}.sql
```

**Example:**

```
V1__create_users_table.sql
V2__add_email_index_to_users.sql
```

### Migration rules

| ✅ Do | ❌ Don't |
| --- | --- |
| Create new migration files | Edit existing migration files |
| Use incremental version numbers | Skip version numbers |
| Test migrations locally first | Push untested migrations |
| Write reversible migrations when possible | Write destructive migrations without a backup plan |

### Coordinating migrations with teammates

If multiple people are working on migrations simultaneously, **communicate** to avoid version number conflicts. Check with the team before claiming a version number.

---

## Resolving Merge Conflicts

The `main` branch is protected against merging PRs that have conflicts. If your PR shows conflicts:

### 1. Update your branch with the latest `main`

```sh
git checkout feature/your-feature-name
git fetch origin
git merge origin/main
```

### 2. Resolve the conflicts

Git will mark conflicted files. Open each one, look for the conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`), and resolve them manually.

### 3. Test after resolving

**Always** re-run the local checks after resolving conflicts:

```sh
# Frontend
cd apps/frontend
npm run lint && npm run check && npm run build

# Backend
cd apps/backend
./mvnw verify
```

### 4. Commit and push

```sh
git add .
git commit -m "chore: resolve merge conflicts with main"
git push
```

> **Tip:** Keep your branches short-lived and merge frequently to minimize conflicts.

---

## Troubleshooting

### CI is failing but it works on my machine

- Make sure you're using the correct versions: **Java 21** and **Node.js 22**.
- CI runs `npm ci` (clean install), not `npm install`. Delete `node_modules` and run `npm ci` locally to reproduce.
- CI runs on **Ubuntu Linux**. File path casing matters — `MyComponent.svelte` and `mycomponent.svelte` are different files.
- If `./mvnw` fails in CI with `bad interpreter` or similar, the wrapper may have Windows (CRLF) line endings. The repo’s `.gitattributes` forces LF for `mvnw`; re-normalize with `git add --renormalize apps/backend/mvnw` and commit.

### My PR shows conflicts but I didn't change those files

Someone else merged changes to `main` that overlap with your files. Follow the [Resolving Merge Conflicts](#resolving-merge-conflicts) section above.

### CodeScene is flagging my changes but I don't think they're problematic

CodeScene's analysis is advisory. If you've considered the feedback and believe your approach is correct, note your reasoning in the PR and proceed. The team can discuss during review.

### The preview deployment failed

Check the GitHub Actions logs linked in the PR comment. Common causes:

- Docker build failure (check the Dockerfile and build output)
- DigitalOcean token issues (contact the team lead)
- Transient network errors (re-run the workflow from the Actions tab)

### I accidentally pushed to main

This shouldn't be possible since `main` is protected, but if branch protection is temporarily disabled:

1. Don't panic.
2. Notify the team immediately.
3. The team lead can revert the commit if needed.