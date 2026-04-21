<script lang="ts">
// Contributor(s): Robbie, Mason, Owen
// Main: Mason - Circular avatar container for profile and review imagery with size presets.
// Assistance: Robbie - Staff chat and inbox layouts that show customer and employee faces.
// Assistance: Owen - Linked-account and photo-review flows that share the same avatar treatment.


	import { Avatar as AvatarPrimitive } from 'bits-ui';
	import { cn } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		loadingStatus = $bindable('loading'),
		size = 'default',
		class: className,
		...restProps
	}: AvatarPrimitive.RootProps & {
		size?: 'default' | 'sm' | 'lg';
	} = $props();
</script>

<AvatarPrimitive.Root
	bind:ref
	bind:loadingStatus
	data-slot="avatar"
	data-size={size}
	class={cn(
		'group/avatar relative flex size-8 shrink-0 rounded-full select-none after:absolute after:inset-0 after:rounded-full after:border after:border-border after:mix-blend-darken data-[size=lg]:size-10 data-[size=sm]:size-6 dark:after:mix-blend-lighten',
		className
	)}
	{...restProps}
/>
