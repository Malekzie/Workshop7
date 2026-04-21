// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Menu composition (products, tags, reviews wiring).
// Assistance: Robbie - Tag and filter service calls that feed this module.
// Assistance: Samantha - Cart actions that start from menu selections.
import { getProducts } from '$lib/services/products';
import { getTags } from '$lib/services/tags';
import { createProductReview, getProductReviews } from '$lib/services/review';
import { truncateModerationMessage } from '$lib/utils/reviewMessage';

/** Tag row shape used by menu filters. Ids align with TagDto from the API. */
export type MenuTag = {
	id: number | string;
	name: string;
};

/** Product card fields sourced from ProductDto JSON. */
export type MenuProduct = {
	id: number | string;
	name: string;
	description?: string | null;
	imageUrl?: string | null;
	basePrice: number | string;
	tagIds?: Array<number | string>;
};

type ReviewSubmissionResult = {
	status?: string;
	moderationMessage?: string | null;
};

/** Loads products and tags in parallel for the menu route. */
export async function loadMenuCatalog() {
	return Promise.all([getProducts(), getTags()]);
}

/** Reads tag search and product query params from the menu URL. */
export function resolveInitialMenuState(url: URL, tags: MenuTag[]) {
	const tagParam = url.searchParams.get('tag');
	let activeTagId = null;

	if (tagParam && tags.some((tag) => String(tag.id) === tagParam)) {
		const byId = tags.find((tag) => String(tag.id) === tagParam);
		const byName = tags.find((tag) => tag.name.toLowerCase() === tagParam.toLowerCase());
		activeTagId = (byId ?? byName)?.id ?? null;
	}

	return {
		activeTagId,
		searchQuery: url.searchParams.get('search') ?? '',
		productId: url.searchParams.get('product')
	};
}

/** Client side filter by active tag id and name or description search text. */
export function filterMenuProducts(
	products: MenuProduct[],
	activeTagId: number | string | null,
	searchQuery: string
) {
	const query = searchQuery.toLowerCase();

	return products.filter((product) => {
		const matchesTag = activeTagId === null || product.tagIds?.includes(activeTagId);
		const matchesSearch =
			!query ||
			(product.name ?? '').toLowerCase().includes(query) ||
			(product.description ?? '').toLowerCase().includes(query);
		return matchesTag && matchesSearch;
	});
}

/** Fetches reviews for the modal using the public product reviews GET. */
export async function loadMenuProductReviews(productId: number | string) {
	return getProductReviews(productId);
}

/** Submits createProductReview then maps moderation status for UI without throwing. */
export async function submitMenuProductReview(options: {
	productId: number | string;
	rating: number;
	comment: string;
	guestName?: string;
}) {
	const submitted = await createProductReview(
		options.productId,
		options.rating,
		options.comment,
		options.guestName
	);

	const status = (submitted?.status ?? '').toLowerCase();
	if (status === 'rejected') {
		const short = truncateModerationMessage(submitted?.moderationMessage);
		return {
			ok: false,
			error: short
				? `Couldn't post review: ${short}`
				: "We couldn't post that review. Try different wording."
		};
	}

	return { ok: true, submitted };
}
