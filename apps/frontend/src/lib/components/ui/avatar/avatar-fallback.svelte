<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Mason - Initials and placeholder fill when profile or review images fail to load.
// Assistance: Robbie - Chat and staff lists that must never show a broken image slot.


	import { Avatar as AvatarPrimitive } from 'bits-ui';
	import { cn } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		class: className,
		...restProps
	}: AvatarPrimitive.FallbackProps = $props();
</script>

<AvatarPrimitive.Fallback
	bind:ref
	data-slot="avatar-fallback"
	class={cn(
		'flex size-full items-center justify-center rounded-full bg-muted text-sm text-muted-foreground group-data-[size=sm]/avatar:text-xs',
		className
	)}
	{...restProps}
/>
