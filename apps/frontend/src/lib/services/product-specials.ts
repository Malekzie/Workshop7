// Contributor(s): Mason
// Main: Mason - Client calls for menu products bakeries tags reviews profile and account.

import { PRODUCT_SPECIALS_API } from '$lib/services/constants';
import type { ProductRecord } from '$lib/services/types';

/** GET all specials for admin or staff grids. */
export async function getAllSpecials(): Promise<ProductRecord[]> {
	const res = await fetch(`${PRODUCT_SPECIALS_API}/all`);
	if (!res.ok) throw new Error(`Failed to fetch specials: ${res.status}`);
	return res.json();
}

/** GET today special with optional yyyy-MM-dd for bakery local pricing. */
export async function getTodaySpecial(date?: string): Promise<ProductRecord> {
	const url = date ? `${PRODUCT_SPECIALS_API}/today?date=${date}` : `${PRODUCT_SPECIALS_API}/today`;
	const res = await fetch(url);
	if (!res.ok) throw new Error(`Failed to fetch today's special: ${res.status}`);
	return res.json();
}

/** GET specials for a calendar day with optional date query. */
export async function getSpecialsForDate(date?: string): Promise<ProductRecord[]> {
	const url = date
		? `${PRODUCT_SPECIALS_API}/for-date?date=${date}`
		: `${PRODUCT_SPECIALS_API}/for-date`;
	const res = await fetch(url);
	if (!res.ok) throw new Error(`Failed to fetch specials for date: ${res.status}`);
	return res.json();
}
