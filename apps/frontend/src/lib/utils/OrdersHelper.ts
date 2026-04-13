/**
 * Helper utilities for order history page
 */

export type BadgeVariant = 'secondary' | 'destructive' | 'default' | 'outline';

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

export function formatPrice(amount: number | null | undefined): string {
	if (amount == null) return '—';
	return (amount / 100).toLocaleString('en-CA', {
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
