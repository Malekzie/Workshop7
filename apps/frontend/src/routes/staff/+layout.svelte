<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Staff chrome: sidebar navigation plus main column for nested staff routes.

	import StaffSidebar from '$lib/components/staff/StaffSidebar.svelte';

	let { children } = $props();
</script>

<div
	class="flex h-[calc(100dvh-var(--app-navbar-height))] flex-col overflow-hidden bg-background md:flex-row"
>
	<StaffSidebar />
	<div class="flex flex-1 flex-col overflow-hidden">
		{@render children()}
	</div>
</div>
