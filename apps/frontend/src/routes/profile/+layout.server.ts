// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Profile subtree requires a signed-in session otherwise redirect to login with return path.

import { redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = ({ locals, url }) => {
	if (!locals.user) {
		redirect(303, `/login?redirectTo=${encodeURIComponent(url.pathname)}`);
	}
	return { user: locals.user };
};
