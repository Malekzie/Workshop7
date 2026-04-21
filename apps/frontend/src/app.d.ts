// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Ambient App Locals and PageData types per https://svelte.dev/docs/kit/types#app.d.ts
declare global {
	namespace App {
		// interface Error {}
		interface Locals {
			user: { username: string; role: 'admin' | 'employee' | 'customer' } | null;
		}
		interface PageData {
			user?: { username: string; role: 'admin' | 'employee' | 'customer' } | null;
		}
		// interface PageState {}
		// interface Platform {}
	}
}

export {};
