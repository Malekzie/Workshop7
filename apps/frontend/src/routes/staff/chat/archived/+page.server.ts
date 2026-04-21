// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Archived chat is admin-only everyone else redirects to the live staff chat route.

import { redirect } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = ({ locals }) => {
    if (locals.user?.role !== 'admin') {
        redirect(303, '/staff/chat');
    }
};
