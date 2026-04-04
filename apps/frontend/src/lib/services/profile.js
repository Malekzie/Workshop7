import { get } from 'svelte/store';
import { user } from '$lib/stores/authStore.js';

const API = '/api/v1';

export async function getProfile() {
	const currentUser = get(user);
	const role = (currentUser?.role ?? '').toLowerCase();

	const url =
		role === 'employee' || role === 'admin' ? `${API}/employee/me` : `${API}/customers/me`;

	const res = await fetch(url, { credentials: 'include' });
	if (!res.ok) throw new Error('Failed to fetch profile: ' + res.status);

	const data = await res.json();

	if (role === 'employee' || role === 'admin') {
		return {
			...data,
			email: data.workEmail ?? null,
			loyaltyTier: null,
			rewardBalance: null
		};
	}

	return data;
}
