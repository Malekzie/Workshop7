import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ locals, fetch }) => {
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
	if (locals.user) {
		const customerRes = await fetch('/api/v1/customers/me');
		if (customerRes.ok) {
			customer = await customerRes.json().catch(() => null);
		}
	}

	return {
		user: locals.user ?? null,
		bakeries: bakeries ?? [],
		stripePublishableKey: (stripeConfig?.publishableKey as string) ?? '',
		customer
	};
};
