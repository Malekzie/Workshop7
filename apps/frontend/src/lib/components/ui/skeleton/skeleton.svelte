<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Pulse placeholders while catalog menu rows and grids fetch async data.
// Assistance: Robbie - Staff tables and inbox lists that reuse the same skeleton rows.
// Assistance: Samantha - Cart and order history lists before line items resolve.


	import { cn, type WithElementRef, type WithoutChildren } from '$lib/utils.js';
	import type { HTMLAttributes } from 'svelte/elements';

	let {
		ref = $bindable(null),
		class: className,
		...restProps
	}: WithoutChildren<WithElementRef<HTMLAttributes<HTMLDivElement>>> = $props();
</script>

<div
	bind:this={ref}
	data-slot="skeleton"
	class={cn('animate-pulse rounded-2xl bg-muted', className)}
	{...restProps}
></div>
