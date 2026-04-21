// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Staff user management routes are admin-only other roles redirect to the staff dashboard.

import { redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = ({ locals }) => {
	if (!locals.user || locals.user.role !== 'admin') {
		redirect(303, '/staff/dashboard');
	}
	return { user: locals.user };
};
