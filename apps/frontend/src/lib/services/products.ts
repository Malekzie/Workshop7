// Contributor(s): Mason
// Main: Mason - Client calls for menu products bakeries tags reviews profile and account.

import { PRODUCTS_API } from '$lib/services/constants';
import type { ApiId, ProductRecord } from '$lib/services/types';

let cache: ProductRecord[] | null = null;

/** GET products list. Uses in-memory cache for the session after first load. */
export async function getProducts(): Promise<ProductRecord[]> {
	if (cache) return cache;

	const res = await fetch(PRODUCTS_API, {
		credentials: 'include'
	});

	if (!res.ok) throw new Error(`Failed to fetch products: ${res.status}`);

	cache = await res.json();
	return cache ?? [];
}

/** GET one product by id for detail views and featured cards. */
export async function getProductById(id: ApiId): Promise<ProductRecord> {
	const res = await fetch(`${PRODUCTS_API}/${id}`, {
		credentials: 'include'
	});
	if (!res.ok) throw new Error(`Failed to fetch product: ${res.status}`);
	return res.json();
}
