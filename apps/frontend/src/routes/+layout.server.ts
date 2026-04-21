// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Root layout load forwards locals.user from the JWT handle hook for child routes and layouts.

import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = ({ locals }) => {
	return { user: locals.user };
};
