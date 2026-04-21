<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Trigger that opens a linked sheet for filters category picks and short forms.
// Assistance: Robbie - Staff row actions that open thread or order side panels.
// Assistance: Samantha - Cart and fulfillment pickers launched from inline actions.


	import { Dialog as SheetPrimitive } from 'bits-ui';

	let { ref = $bindable(null), ...restProps }: SheetPrimitive.TriggerProps = $props();
</script>

<SheetPrimitive.Trigger bind:ref data-slot="sheet-trigger" {...restProps} />
