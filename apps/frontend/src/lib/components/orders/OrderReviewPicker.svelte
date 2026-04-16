<script lang="ts">
	/**
	 * Modal for selecting which items to review
	 * Displays order and product items with their review status
	 */
	import type { ReviewableItem } from '$lib/utils/OrdersHelper';
	import type { OrderRecord } from '$lib/services/types';

	interface PickerState {
		order: OrderRecord;
		items: ReviewableItem[];
	}

	interface Props {
		picker: PickerState | null;
		onSelectItem?: (item: ReviewableItem) => void;
		onClose?: () => void;
	}

	let { picker = null, onSelectItem = () => {}, onClose = () => {} }: Props = $props();
</script>

{#if picker}
	<div class="fixed inset-0 z-40 bg-black/50" onclick={onClose} role="presentation"></div>
	<div class="fixed inset-0 z-50 flex items-center justify-center p-4">
		<div class="w-full max-w-md rounded-2xl border border-border bg-card p-6 shadow-xl">
			<h2 class="text-lg font-bold text-foreground">What would you like to review?</h2>
			<p class="mt-1 text-sm text-muted-foreground">
				Order #{picker.order.orderNumber}
			</p>
			<div class="mt-5 space-y-2">
				{#each picker.items as item (item.id + item.type)}
					{#if !item.done}
						<button
							onclick={() => onSelectItem(item)}
							class="flex w-full items-center justify-between rounded-xl border border-border px-4 py-3 text-left transition-colors hover:bg-muted"
						>
							<span class="text-sm font-medium text-foreground">{item.label}</span>
							<span class="text-xs text-primary">Review →</span>
						</button>
					{:else if item.failed}
						<div
							class="flex w-full items-center justify-between rounded-xl border border-amber-200 bg-amber-50/80 px-4 py-3"
						>
							<span class="text-sm font-medium text-foreground">{item.label}</span>
							<span class="text-xs font-medium text-amber-800">Not posted</span>
						</div>
					{:else}
						<div
							class="flex w-full items-center justify-between rounded-xl border border-border bg-muted px-4 py-3 opacity-50"
						>
							<span class="text-sm font-medium text-foreground">{item.label}</span>
							<span class="text-xs text-green-600">✓ Reviewed</span>
						</div>
					{/if}
				{/each}
			</div>
			<button
				onclick={onClose}
				class="mt-4 w-full px-4 py-2 text-sm text-muted-foreground hover:text-foreground"
			>
				Done
			</button>
		</div>
	</div>
{/if}
