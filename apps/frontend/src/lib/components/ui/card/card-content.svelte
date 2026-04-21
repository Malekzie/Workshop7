<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Main content region inside a card for body text lists and forms.
// Assistance: Robbie - Staff card bodies for thread previews and order line tables.
// Assistance: Samantha - Line items fees and address blocks inside checkout cards.


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
	data-slot="card-content"
	class={cn('px-6 group-data-[size=sm]/card:px-4', className)}
	{...restProps}
>
	{@render children?.()}
</div>
