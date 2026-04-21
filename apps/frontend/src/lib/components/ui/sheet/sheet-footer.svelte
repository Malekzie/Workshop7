<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Footer slot for primary and secondary actions at the bottom of a sheet.
// Assistance: Robbie - Staff confirm and cancel pairs on moderation and inbox sheets.
// Assistance: Samantha - Pay schedule and review actions inside checkout sheets.


	import { cn, type WithElementRef } from '$lib/utils.js';
	import type { HTMLAttributes } from 'svelte/elements';

	let {
		ref = $bindable(null),
		class: className,
		children,
		...restProps
	}: WithElementRef<HTMLAttributes<HTMLDivElement>> = $props();
</script>

<div
	bind:this={ref}
	data-slot="sheet-footer"
	class={cn('mt-auto flex flex-col gap-2 p-6', className)}
	{...restProps}
>
	{@render children?.()}
</div>
