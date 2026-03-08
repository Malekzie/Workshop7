# Frontend — Peelin' Good Web Client

Detailed documentation for the SvelteKit frontend located in `apps/frontend/`.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup & Running Locally](#setup--running-locally)
- [Routing](#routing)
- [Styling](#styling)
- [Components](#components)
- [Linting & Formatting](#linting--formatting)
- [Type Checking](#type-checking)
- [Docker](#docker)
- [CI/CD](#cicd)
- [Common Issues](#common-issues)

---

## Architecture Overview

The frontend is a server-side rendered (SSR) web application built with **SvelteKit 2** and **Svelte 5**. It uses:

- **SvelteKit** with `adapter-node` for server-side rendering and file-based routing
- **Tailwind CSS 4** for utility-first styling
- **shadcn-svelte** conventions for reusable UI components
- **TypeScript** with strict mode enabled
- **Vite 7** as the build tool and dev server

---

## Tech Stack

| Technology | Version | Purpose |
| --- | --- | --- |
| Node.js | 22+ | Runtime |
| SvelteKit | 2.x | Application framework |
| Svelte | 5.x | UI component framework |
| TypeScript | 5.x | Type safety |
| Tailwind CSS | 4.x | Styling |
| Vite | 7.x | Build tool / dev server |
| ESLint | 9.x | Code linting |
| Prettier | 3.x | Code formatting |
| Lucide Svelte | Latest | Icon library |
| tailwind-variants | 3.x | Component variant styling |
| tailwind-merge | 3.x | Class name merging |
| tw-animate-css | 1.x | Animation utilities |

---

## Project Structure

```
apps/frontend/
├── src/
│   ├── app.d.ts                   # Global type declarations
│   ├── app.html                   # HTML shell template
│   ├── lib/
│   │   ├── assets/                # Static assets (favicon, images)
│   │   │   └── favicon.svg
│   │   ├── components/
│   │   │   └── ui/                # Reusable UI components (shadcn-svelte)
│   │   ├── hooks/                 # Custom Svelte hooks / shared logic
│   │   ├── index.ts               # Barrel exports for $lib
│   │   └── utils.ts               # Utility helpers (cn, type helpers)
│   └── routes/                    # SvelteKit file-based routing
│       ├── +layout.svelte         # Root layout (global CSS, favicon)
│       ├── +page.svelte           # Home page
│       └── layout.css             # Global Tailwind CSS + theme variables
├── static/                        # Publicly served static files
│   └── robots.txt
├── Dockerfile                     # Multi-stage Docker build
├── package.json                   # Dependencies and scripts
├── package-lock.json              # Locked dependency versions
├── svelte.config.js               # SvelteKit configuration
├── vite.config.ts                 # Vite configuration (Tailwind, SvelteKit, devtools)
├── tsconfig.json                  # TypeScript configuration
├── eslint.config.js               # ESLint flat config
├── .gitignore                     # Frontend-specific git ignores
├── .npmrc                         # npm config (engine-strict=true)
├── .prettierrc                    # Prettier configuration
├── .prettierignore                # Files excluded from Prettier
└── README.md                      # SvelteKit scaffold README
```

---

## Setup & Running Locally

### Prerequisites

- **Node.js 22+** — use [nvm](https://github.com/nvm-sh/nvm) or [fnm](https://github.com/Schniz/fnm) to manage versions
- **npm** — comes with Node.js

> **Note:** The `.npmrc` has `engine-strict=true`, so npm will refuse to install if your Node version doesn't match the expected range.

### 1. Install dependencies

```sh
cd apps/frontend
npm install
```

### 2. Start the dev server

```sh
npm run dev
```

The app will start on **http://localhost:5173** with hot module replacement (HMR) enabled.

To automatically open the browser:

```sh
npm run dev -- --open
```

---

## Routing

SvelteKit uses **file-based routing**. All routes live under `src/routes/`.

### Key files

| File | Purpose |
| --- | --- |
| `+page.svelte` | A page component — renders at the URL matching its directory |
| `+layout.svelte` | A layout wrapper — wraps all pages in the same directory and subdirectories |
| `+page.ts` / `+page.server.ts` | Load function — fetches data before rendering the page |
| `+layout.ts` / `+layout.server.ts` | Layout load function — fetches data shared across nested pages |
| `+error.svelte` | Error page — displayed when a load function throws |
| `+server.ts` | API route — handles HTTP requests (GET, POST, etc.) |

### Example

To add a new page at `/about`:

```
src/routes/about/+page.svelte
```

```svelte
<h1>About Peelin' Good</h1>
<p>We're all about peelin' good.</p>
```

For the full routing reference, see the [SvelteKit docs on routing](https://svelte.dev/docs/kit/routing).

---

## Styling

### Tailwind CSS 4

The project uses **Tailwind CSS 4** configured via Vite (not PostCSS). The Tailwind plugin is registered in `vite.config.ts`:

```ts
import tailwindcss from '@tailwindcss/vite';
export default defineConfig({ plugins: [tailwindcss(), sveltekit(), devtoolsJson()] });
```

### Global styles & theming

The global stylesheet is at `src/routes/layout.css`. It includes:

- Tailwind base import (`@import "tailwindcss"`)
- Animation utilities (`@import "tw-animate-css"`)
- CSS custom properties for **light** and **dark** themes (`:root` and `.dark`)
- A `@theme inline` block mapping CSS variables to Tailwind color tokens

To toggle dark mode, add the `dark` class to a parent element. The custom variant is defined as:

```css
@custom-variant dark (&:is(.dark *));
```

### Utility: `cn()`

Use the `cn()` function from `$lib/utils.ts` to merge Tailwind classes safely:

```svelte
<script lang="ts">
  import { cn } from '$lib/utils';
</script>

<div class={cn('p-4 bg-primary', someCondition && 'bg-destructive')}>
  ...
</div>
```

This uses `clsx` for conditional classes and `tailwind-merge` to resolve conflicting utilities.

---

## Components

### UI components (`$lib/components/ui/`)

The project follows **shadcn-svelte** conventions. UI components are placed in `src/lib/components/ui/` and are meant to be copied in and customized — they are not installed from a package.

To add new shadcn-svelte components, use:

```sh
npx shadcn-svelte@latest add <component-name>
```

Or manually create components in the `ui/` directory following the established patterns.

### Icons

The project uses **Lucide Svelte** for icons:

```svelte
<script lang="ts">
  import { Home } from '@lucide/svelte';
</script>

<Home class="h-5 w-5" />
```

Browse all available icons at [lucide.dev](https://lucide.dev/).

### Type helpers

`$lib/utils.ts` exports several type helpers for building component props:

| Type | Purpose |
| --- | --- |
| `WithoutChild<T>` | Removes `child` prop from a type |
| `WithoutChildren<T>` | Removes `children` prop from a type |
| `WithoutChildrenOrChild<T>` | Removes both `child` and `children` props |
| `WithElementRef<T, U>` | Adds an optional `ref` prop for element binding |

These are commonly used when wrapping headless component libraries.

---

## Linting & Formatting

### ESLint

The ESLint configuration is in `eslint.config.js` using the **flat config** format (ESLint 9). It includes:

- `@eslint/js` recommended rules
- `typescript-eslint` recommended rules
- `eslint-plugin-svelte` recommended + Prettier rules
- `eslint-config-prettier` to disable formatting-related rules
- `no-undef` is turned **off** (TypeScript handles this)

### Prettier

Prettier is configured in `.prettierrc`:

| Option | Value |
| --- | --- |
| `useTabs` | `true` |
| `singleQuote` | `true` |
| `trailingComma` | `"none"` |
| `printWidth` | `100` |
| Plugins | `prettier-plugin-svelte`, `prettier-plugin-tailwindcss` |

Files excluded from Prettier are listed in `.prettierignore` (lock files, `/static/`).

### Running locally

```sh
# Check for linting and formatting issues
npm run lint

# Auto-fix formatting issues
npm run format
```

> **Important:** Linting is enforced in CI. PRs that fail `npm run lint` will not pass the pipeline.

---

## Type Checking

TypeScript is configured in `tsconfig.json` with **strict mode** enabled. SvelteKit generates its own base `tsconfig.json` inside `.svelte-kit/`, which the project config extends.

### Running type checks

```sh
# One-time check
npm run check

# Watch mode (re-checks on file changes)
npm run check:watch
```

This runs `svelte-check` which validates both TypeScript and Svelte files.

> **Important:** Type checking is enforced in CI. PRs that fail `npm run check` will not pass the pipeline.

---

## Docker

The frontend uses a **multi-stage Dockerfile** for production builds:

### Stage 1 — Build

- Base image: `node:22-alpine`
- Runs `npm ci` for reproducible dependency installs
- Runs `npm run build` to produce the SvelteKit output
- Prunes dev dependencies with `npm prune --production`

### Stage 2 — Runtime

- Base image: `node:22-alpine`
- Runs as a non-root user (`appuser`)
- Copies only the `build/` output, `node_modules/`, and `package.json`
- Exposes port `3000`
- Includes a **health check** that hits `http://localhost:3000` every 30 seconds
- Entry point: `node build`

### Build locally

```sh
cd apps/frontend
docker build -t peelin-frontend .
docker run -p 3000:3000 peelin-frontend
```

The app will be available at **http://localhost:3000**.

---

## CI/CD

The frontend CI is defined in `.github/workflows/frontend-build.yml`. It triggers **only** when files under `apps/frontend/` change.

### On pull request

1. Checkout code
2. Set up Node.js 22 with npm cache
3. `npm ci` — install dependencies
4. `npm run lint` — check linting and formatting
5. `npm run check` — TypeScript / Svelte type checking
6. `npm run build` — production build
7. `docker build` — verify the Docker image builds
8. Deploy a **PR preview** to DigitalOcean
9. Comment the preview URL on the PR (or link to failure logs)

### On push to main

1. Same lint, check, and build steps as PR
2. Build and **push** Docker image to GitHub Container Registry (`ghcr.io`)
3. **Deploy to production** on DigitalOcean

### CodeScene Delta Analysis

CodeScene runs on PRs and will comment with findings about:

- Code health changes in modified files
- Complexity hotspots
- Functions growing too large
- Potential coupling issues

---

## Common Issues

### "engine" compatibility error on `npm install`

The `.npmrc` enforces `engine-strict=true`. Make sure you're running **Node.js 22+**:

```sh
node --version   # Should be v22.x.x or higher
```

Use [nvm](https://github.com/nvm-sh/nvm) or [fnm](https://github.com/Schniz/fnm) to switch versions:

```sh
nvm install 22
nvm use 22
```

### Port 5173 already in use

Vite will automatically try the next available port. If you want a specific port:

```sh
npm run dev -- --port 3000
```

### Tailwind classes not applying

- Make sure you're importing `layout.css` through the root layout (`+layout.svelte`). This is already done via the `import './layout.css'` statement.
- If you added a new custom utility or theme token, restart the dev server.
- Use the `cn()` helper when combining conditional classes to avoid conflicts.

### `svelte-check` errors on fresh clone

Run the prepare script first to sync SvelteKit's generated types:

```sh
npm run prepare
```

This generates the `.svelte-kit/` directory with type definitions that `tsconfig.json` depends on.

### Prettier and ESLint conflicts

The ESLint config already includes `eslint-config-prettier` to disable conflicting rules. If you see formatting-related lint errors:

1. Run `npm run format` first to auto-fix formatting
2. Then run `npm run lint` to check for remaining issues

### Docker build fails locally

Make sure you're running the Docker build from the `apps/frontend/` directory (the Dockerfile expects `package.json` at the root of the build context):

```sh
cd apps/frontend
docker build -t peelin-frontend .
```
