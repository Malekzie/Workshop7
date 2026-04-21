<script lang="ts">
// Contributor(s): Mason
// Main: Mason - Dismiss control wired to the sheet close handler for keyboard and pointer users.


	import { Dialog as SheetPrimitive } from 'bits-ui';

	let { ref = $bindable(null), ...restProps }: SheetPrimitive.CloseProps = $props();
</script>

<SheetPrimitive.Close bind:ref data-slot="sheet-close" {...restProps} />
