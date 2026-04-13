<script>
	/**
	 * Toast notification component - displays a temporary notification message
	 */
	let { message = null } = $props();
</script>

{#if message}
	<div
		class="fixed bottom-6 left-1/2 z-200 max-w-md -translate-x-1/2 rounded-xl border border-border bg-card px-4 py-3 text-sm text-foreground shadow-lg"
		role="status"
	>
		{message}
	</div>
{/if}
