<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Muted description text under card titles for hints and secondary detail.
// Assistance: Robbie - Staff helper copy on approval and messaging cards.
// Assistance: Samantha - Fee breakdown hints and schedule notes on checkout cards.


	import type { HTMLAttributes } from 'svelte/elements';
	import { cn, type WithElementRef } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		class: className,
		children,
		...restProps
	}: WithElementRef<HTMLAttributes<HTMLParagraphElement>> = $props();
</script>

<p
	bind:this={ref}
	data-slot="card-description"
	class={cn('text-sm text-muted-foreground', className)}
	{...restProps}
>
	{@render children?.()}
</p>
