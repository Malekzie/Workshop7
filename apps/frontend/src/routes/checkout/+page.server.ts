import { redirect, error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

// Page-level guard (not layout) so /guest/checkout remains accessible without auth.
export const load: PageServerLoad = async ({ locals, url, fetch }) => {
	if (!locals.user) {
		redirect(303, `/login?redirectTo=${encodeURIComponent(url.pathname)}`);
	}

	const [bakeriesRes, stripeRes] = await Promise.all([
		fetch('/api/v1/bakeries'),
		fetch('/api/v1/stripe/config').catch(() => null)
	]);

	if (!bakeriesRes.ok) {
		error(503, 'Could not load checkout data. Please try again.');
	}

	const [bakeries, stripeConfig] = await Promise.all([
		bakeriesRes.json(),
		stripeRes?.ok ? stripeRes.json().catch(() => ({})) : Promise.resolve({})
	]);

	let customer = null;
	const customerRes = await fetch('/api/v1/customers/me');
	if (customerRes.ok) {
		customer = await customerRes.json().catch(() => null);
	}

	return {
		user: locals.user,
		bakeries: bakeries ?? [],
		stripePublishableKey: (stripeConfig?.publishableKey as string) ?? '',
		customer
	};
};
