import type { LayoutServerLoad } from './$types';

// Cart is accessible to guests — they proceed to /guest/checkout.
// No auth required; data.user comes from root layout and may be null for guests.
export const load: LayoutServerLoad = ({ locals }) => {
	return { user: locals.user };
};
