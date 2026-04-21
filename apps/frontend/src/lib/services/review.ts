// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Product review fetch and submit helpers.
// Assistance: Robbie - Staff review approval paths that reuse these endpoints.
// Assistance: Samantha - Order completion flows that prompt for product reviews.
import { apiFetch } from '$lib/utils/api';
import { ORDERS_API, PRODUCTS_API, REVIEWS_API } from '$lib/services/constants';
import type {
	ApiId,
	ReviewRecord,
	ReviewSubmissionResult,
	ErrorWithStatus
} from '$lib/services/types';

/** GET top reviews for home or marketing surfaces. */
export async function getTopReviews(limit = 3): Promise<ReviewRecord[]> {
	const res = await fetch(`${REVIEWS_API}/top?limit=${limit}`);
	if (!res.ok) throw new Error(`Failed to fetch reviews: ${res.status}`);
	return res.json();
}

/** POST product reviews with optional guestName and orderId fields like ReviewCreateRequest in OpenAPI. */
export async function createProductReview(
	productId: ApiId,
	rating: number,
	comment: string,
	guestName?: string,
	orderId: ApiId | null = null
): Promise<ReviewSubmissionResult | undefined> {
	const res = await apiFetch(`${PRODUCTS_API}/${productId}/reviews`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			rating,
			comment,
			...(guestName ? { guestName } : {}),
			...(orderId ? { orderId } : {})
		})
	});

	if (!res) return;
	if (!res.ok) {
		const err = (await res.json().catch(() => ({}))) as { message?: string };
		const error = new Error(
			err.message ?? `Failed to submit review: ${res.status}`
		) as ErrorWithStatus;
		error.status = res.status;
		throw error;
	}
	return res.json();
}

/** POST order linked review on orders id reviews path for signed-in flows. */
export async function createOrderReview(
	orderId: ApiId,
	rating: number,
	comment: string
): Promise<ReviewSubmissionResult | undefined> {
	const res = await apiFetch(`${ORDERS_API}/${orderId}/reviews`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ rating, comment, orderId })
	});
	if (!res) return;
	if (!res.ok) {
		const err = (await res.json().catch(() => ({}))) as { message?: string };
		const error = new Error(
			err.message ?? `Failed to submit review: ${res.status}`
		) as ErrorWithStatus;
		error.status = res.status;
		throw error;
	}
	return res.json();
}

/** GET approved reviews for one product id. */
export async function getProductReviews(productId: ApiId): Promise<ReviewRecord[]> {
	const res = await fetch(`${PRODUCTS_API}/${productId}/reviews`);

	if (!res.ok) throw new Error(`Failed to fetch reviews: ${res.status}`);
	return res.json();
}
