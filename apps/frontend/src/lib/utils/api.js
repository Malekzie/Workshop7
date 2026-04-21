// Contributor(s): Robbie
// Main: Robbie - Shared fetch redirect and legacy API helpers with credentials.

import { clearAuth, user } from '$lib/stores/authStore';
import { get } from 'svelte/store';

/** Fetch with cookies. On 401 clears auth and redirects using JSON reason then returns null. */
export async function apiFetch(url, options = {}) {
	const res = await fetch(url, {
		credentials: 'include',
		...options
	});

	if (res.status === 401) {
		const body = await res.json().catch(() => ({}));
		clearAuth();
		await fetch('/api/v1/auth/logout', { method: 'POST', credentials: 'include' });
		if (body.reason === 'deactivated') {
			window.location.href = '/deactivated';
		} else if (body.reason === 'expired') {
			window.location.href = '/login?reason=expired';
		} else {
			window.location.href = '/login';
		}
		return null;
	}

	return res;
}
