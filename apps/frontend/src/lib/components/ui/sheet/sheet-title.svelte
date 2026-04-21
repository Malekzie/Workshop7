<script lang="ts">
// Contributor(s): Mason
// Main: Mason - Title slot for sheet headers on browse filters profile fields and staff settings.


	import { Dialog as SheetPrimitive } from 'bits-ui';
	import { cn } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		class: className,
		...restProps
	}: SheetPrimitive.TitleProps = $props();
</script>

<SheetPrimitive.Title
	bind:ref
	data-slot="sheet-title"
	class={cn('font-heading text-base font-medium text-foreground', className)}
	{...restProps}
/>
