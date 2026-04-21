<script lang="ts">
// Contributor(s): Mason, Owen
// Main: Mason - Portal target so sheet layers mount above page chrome and modals.
// Assistance: Owen - Finder and map flows that need sheets above map tiles without clipping.


	import { Dialog as SheetPrimitive } from 'bits-ui';

	let { ...restProps }: SheetPrimitive.PortalProps = $props();
</script>

<SheetPrimitive.Portal {...restProps} />
