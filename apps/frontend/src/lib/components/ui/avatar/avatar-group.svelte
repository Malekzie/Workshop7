<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Mason - Overlapping avatar stack for thread participants or small team lists.
// Assistance: Robbie - Staff inbox rows that summarize everyone on a conversation.


	import type { HTMLAttributes } from 'svelte/elements';
	import { cn, type WithElementRef } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		class: className,
		children,
		...restProps
	}: WithElementRef<HTMLAttributes<HTMLDivElement>> = $props();
</script>

<div
	bind:this={ref}
	data-slot="avatar-group"
	class={cn(
		'cn-avatar-group group/avatar-group flex -space-x-2 *:data-[slot=avatar]:ring-2 *:data-[slot=avatar]:ring-background',
		className
	)}
	{...restProps}
>
	{@render children?.()}
</div>
