<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Title typography for product names section labels and profile headings inside cards.
// Assistance: Robbie - Staff titles on moderation and queue cards.
// Assistance: Samantha - Order numbers and checkout step titles on payment cards.


	import type { HTMLAttributes } from 'svelte/elements';
	import { cn, type WithElementRef } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		class: className,
		children,
		...restProps
	}: WithElementRef<HTMLAttributes<HTMLDivElement>> = $props();
</script>

<div
	bind:this={ref}
	data-slot="card-title"
	class={cn('font-heading text-base font-medium', className)}
	{...restProps}
>
	{@render children?.()}
</div>
