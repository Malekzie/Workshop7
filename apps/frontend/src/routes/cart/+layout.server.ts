// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Cart layout stays public while passing through user when the cookie session exists.

import type { LayoutServerLoad } from './$types';

// Cart is accessible to guests  -  they proceed to /checkout.
// No auth required; data.user comes from root layout and may be null for guests.
export const load: LayoutServerLoad = ({ locals }) => {
	return { user: locals.user };
};
