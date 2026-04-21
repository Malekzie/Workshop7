<script lang="ts">
// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Action slot aligned with card headers for icon buttons and menus.
// Assistance: Robbie - Staff overflow menus on inbox and dashboard cards.
// Assistance: Samantha - Quick actions on cart and order preview cards before payment.


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
	data-slot="card-action"
	class={cn(
		'cn-card-action col-start-2 row-span-2 row-start-1 self-start justify-self-end',
		className
	)}
	{...restProps}
>
	{@render children?.()}
</div>
