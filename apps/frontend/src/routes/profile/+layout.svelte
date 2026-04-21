<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Profile chrome: customer sidebar plus main column for nested profile routes.

	import ProfileSidebar from '$lib/components/profile/ProfileSidebar.svelte';

	let { children } = $props();
</script>

<div
	class="flex h-[calc(100dvh-var(--app-navbar-height))] flex-col overflow-hidden bg-background md:flex-row"
>
	<ProfileSidebar />
	<div class="flex flex-1 flex-col overflow-hidden">
		{@render children()}
	</div>
</div>
