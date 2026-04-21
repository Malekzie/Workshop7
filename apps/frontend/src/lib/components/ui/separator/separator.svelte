<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Horizontal and vertical rules between sections on profile and marketing pages.
// Assistance: Robbie - Staff settings layouts that separate role blocks and tools.
// Assistance: Samantha - Checkout summaries that separate subtotals fees delivery and tax rows.


	import { Separator as SeparatorPrimitive } from 'bits-ui';
	import { cn } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		class: className,
		'data-slot': dataSlot = 'separator',
		...restProps
	}: SeparatorPrimitive.RootProps = $props();
</script>

<SeparatorPrimitive.Root
	bind:ref
	data-slot={dataSlot}
	class={cn(
		'shrink-0 bg-border data-[orientation=horizontal]:h-px data-[orientation=horizontal]:w-full data-[orientation=vertical]:w-px',
		// this is different in shadcn/ui but self-stretch breaks things for us
		'data-[orientation=vertical]:h-full',
		className
	)}
	{...restProps}
/>
