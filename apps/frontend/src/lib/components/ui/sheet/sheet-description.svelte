<script lang="ts">
// Contributor(s): Mason
// Main: Mason - Subtitle region under sheet titles for helper copy and validation hints.


	import { Dialog as SheetPrimitive } from 'bits-ui';
	import { cn } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		class: className,
		...restProps
	}: SheetPrimitive.DescriptionProps = $props();
</script>

<SheetPrimitive.Description
	bind:ref
	data-slot="sheet-description"
	class={cn('text-sm text-muted-foreground', className)}
	{...restProps}
/>
