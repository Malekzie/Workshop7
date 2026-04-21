<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Inline label segment between grouped buttons for units or modes.
// Assistance: Robbie - Staff toolbar labels on messaging and review tools.
// Assistance: Samantha - Currency or interval labels next to checkout quantity groups.


	import { cn, type WithElementRef } from '$lib/utils.js';
	import type { HTMLAttributes } from 'svelte/elements';
	import type { Snippet } from 'svelte';

	let {
		ref = $bindable(null),
		class: className,
		child,
		...restProps
	}: WithElementRef<HTMLAttributes<HTMLDivElement>> & {
		child?: Snippet<[{ props: Record<string, unknown> }]>;
	} = $props();

	const mergedProps = $derived({
		...restProps,
		class: cn(
			"bg-muted gap-2 rounded-4xl border px-2.5 text-sm font-medium [&_svg:not([class*='size-'])]:size-4 flex items-center [&_svg]:pointer-events-none",
			className
		),
		'data-slot': 'button-group-text'
	});
</script>

{#if child}
	{@render child({ props: mergedProps })}
{:else}
	<div bind:this={ref} {...mergedProps}>
		{@render mergedProps.children?.()}
	</div>
{/if}
