<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Sheet root with focus handling for slide-over panels on narrow layouts.
// Assistance: Robbie - Staff views that prefer sheets over full-page dialogs on small screens.
// Assistance: Samantha - Checkout and cart panels that slide in without dropping parent state.


	import { Dialog as SheetPrimitive } from 'bits-ui';

	let { open = $bindable(false), ...restProps }: SheetPrimitive.RootProps = $props();
</script>

<SheetPrimitive.Root bind:open {...restProps} />
