<script lang="ts">
// Contributor(s): Mason, Owen
// Main: Mason - Dimmed backdrop that captures clicks behind an open sheet.
// Assistance: Owen - Location and map pages where backdrop z-order must clear map controls cleanly.


	import { Dialog as SheetPrimitive } from 'bits-ui';
	import { cn } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		class: className,
		...restProps
	}: SheetPrimitive.OverlayProps = $props();
</script>

<SheetPrimitive.Overlay
	bind:ref
	data-slot="sheet-overlay"
	class={cn('fixed inset-0 z-50 bg-black/30 supports-backdrop-filter:backdrop-blur-sm', className)}
	{...restProps}
/>
