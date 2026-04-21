// Contributor(s): Robbie
// Main: Robbie - SvelteKit fetch helpers for staff tools auth chat and shared API constants.

import { DASHBOARD_API } from '$lib/services/constants';

/** GET admin dashboard summary. JSON matches DashboardSummaryDto in OpenAPI. */
export async function getDashboardSummary(): Promise<Record<string, unknown>> {
	const res = await fetch(`${DASHBOARD_API}/summary`, {
		credentials: 'include'
	});
	if (!res.ok) throw new Error('Failed to fetch dashboard summary');
	return res.json();
}
