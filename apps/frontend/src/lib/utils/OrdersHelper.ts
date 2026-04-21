// Contributor(s): Samantha
// Main: Samantha - Money and order display helpers for checkout and history.

/**
 * Order history and review UI helpers: map API order status strings to badge variants, format CAD dates,
 * and build reviewable slots from order DTO fields returned by GET my orders style endpoints.
 */

export type BadgeVariant = 'secondary' | 'destructive' | 'default' | 'outline';

/** Maps backend order status values to shadcn-style badge variants on the orders list. */
export function statusColor(status: string): BadgeVariant {
	switch (status) {
		case 'pending_payment':
			return 'secondary';
		case 'paid':
		case 'preparing':
			return 'outline';
		case 'ready':
			return 'default';
		case 'delivered':
		case 'picked_up':
		case 'completed':
			return 'default';
		case 'cancelled':
			return 'destructive';
		default:
			return 'secondary';
	}
}

export function formatDate(dateStr: string | null | undefined): string {
	if (!dateStr) return '—';
	return new Date(dateStr).toLocaleDateString('en-CA', {
		year: 'numeric',
		month: 'short',
		day: 'numeric'
	});
}

/** CAD currency string for history rows when amount is present. */
export function formatPrice(amount: number | null | undefined): string {
	if (amount == null) return '—';
	return amount.toLocaleString('en-CA', {
		style: 'currency',
		currency: 'CAD',
		minimumFractionDigits: 2,
		maximumFractionDigits: 2
	});
}

export interface ReviewableItem {
	type: 'order' | 'product';
	id: string | number;
	label: string;
	done: boolean;
	failed: boolean;
}

export function orderHasAnyReviewableSlot(order: any): boolean {
	if (!order || order.status !== 'completed') return false;
	const locDone = order.locationReviewSubmitted === true;
	const lineItems = order.items ?? [];
	if (!locDone) return true;
	if (lineItems.length === 0) return false;
	return lineItems.some((i: any) => i.productReviewSubmitted !== true);
}

/** Flattens one order into review rows for the overlay including bakery experience and each product line. */
export function buildReviewableItems(order: any): ReviewableItem[] {
	return [
		{
			type: 'order',
			id: order.id,
			label: `Order experience at ${order.bakeryName ?? "Peelin' Good"}`,
			done: order.locationReviewSubmitted === true,
			failed: false
		},
		...(order.items ?? []).map((i: any) => ({
			type: 'product',
			id: i.productId,
			label: i.productName,
			done: i.productReviewSubmitted === true,
			failed: false
		}))
	];
}
