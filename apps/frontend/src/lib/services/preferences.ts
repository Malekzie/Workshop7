// Contributor(s): Mason
// Main: Mason - Client calls for menu products bakeries tags reviews profile and account.

import { apiFetch } from '$lib/utils/api';
import { PREFERENCES_API } from '$lib/services/constants';

/** GET customers me preferences. Response rows align with CustomerPreferenceDto in OpenAPI. */
export async function getMyPreferences(): Promise<Record<string, unknown> | undefined> {
	const res = await apiFetch(PREFERENCES_API);

	if (!res) return;
	if (!res.ok) throw new Error(`Failed to fetch preferences: ${res.status}`);
	return res.json();
}

/** PUT customers me preferences. Wraps entries in preferences key like CustomerPreferenceSaveRequest. */
export async function saveMyPreferences(
	preferences: unknown
): Promise<Record<string, unknown> | undefined> {
	const res = await apiFetch(PREFERENCES_API, {
		method: 'PUT',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ preferences })
	});

	if (!res) return;
	if (!res.ok) throw new Error(`Failed to save preferences: ${res.status}`);
	return res.json();
}
