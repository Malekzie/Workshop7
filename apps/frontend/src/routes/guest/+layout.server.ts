// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Guest order routes stay public and still receive optional user for shared chrome.

import type { LayoutServerLoad } from './$types';

// Guest routes  -  no auth required. User passed through if available.
export const load: LayoutServerLoad = ({ locals }) => {
	return { user: locals.user ?? null };
};
