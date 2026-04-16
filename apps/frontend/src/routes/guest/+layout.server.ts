import type { LayoutServerLoad } from './$types';

// Guest routes — no auth required. User passed through if available.
export const load: LayoutServerLoad = ({ locals }) => {
	return { user: locals.user ?? null };
};
