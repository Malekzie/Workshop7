<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Footer strip for secondary actions and meta links under card bodies.
// Assistance: Robbie - Staff footers with audit links on review and order cards.
// Assistance: Samantha - Footers with pay links and policy hints on checkout cards.


	import { cn, type WithElementRef } from '$lib/utils.js';
	import type { HTMLAttributes } from 'svelte/elements';

	let {
		ref = $bindable(null),
		class: className,
		children,
		...restProps
	}: WithElementRef<HTMLAttributes<HTMLDivElement>> = $props();
</script>

<div
	bind:this={ref}
	data-slot="card-footer"
	class={cn(
		'flex items-center rounded-b-4xl px-6 group-data-[size=sm]/card:px-4 [.border-t]:pt-6 group-data-[size=sm]/card:[.border-t]:pt-4',
		className
	)}
	{...restProps}
>
	{@render children?.()}
</div>
