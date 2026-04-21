<script lang="ts">
// Contributor(s): Robbie, Mason, Owen
// Main: Mason - Rounded image element with lazy loading hooks for profile and review media.
// Assistance: Robbie - Staff directory photos sized to match chat density.
// Assistance: Owen - Account-linked headshots that must stay consistent across customer and staff views.


	import { Avatar as AvatarPrimitive } from 'bits-ui';
	import { cn } from '$lib/utils.js';

	let {
		ref = $bindable(null),
		class: className,
		...restProps
	}: AvatarPrimitive.ImageProps = $props();
</script>

<AvatarPrimitive.Image
	bind:ref
	data-slot="avatar-image"
	class={cn('aspect-square size-full rounded-full object-cover', className)}
	{...restProps}
/>
