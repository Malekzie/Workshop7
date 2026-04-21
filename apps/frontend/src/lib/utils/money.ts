// Contributor(s): Samantha
// Main: Samantha - Money and order display helpers for checkout and history.

/** Display a Canadian dollar amount with literal CAD suffix for storefront text. */
export function formatPriceCad(amount: number | string | null | undefined): string {
	const n = Number(amount);
	if (!Number.isFinite(n)) return '$0.00 CAD';
	return `$${n.toFixed(2)} CAD`;
}

/** Discount line with minus prefix and CAD suffix for cart summaries. */
export function formatDiscountCad(amount: number | string | null | undefined): string {
	const n = Math.abs(Number(amount));
	if (!Number.isFinite(n)) return '−$0.00 CAD';
	return `−$${n.toFixed(2)} CAD`;
}
