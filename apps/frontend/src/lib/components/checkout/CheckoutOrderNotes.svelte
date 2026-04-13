<script lang="ts">
	let { value = $bindable('') }: { value?: string } = $props();
</script>

<section class="rounded-xl border border-border bg-card p-6 shadow-sm">
	<h2 class="mb-4 text-lg font-semibold text-foreground">Order Notes</h2>
	<textarea
		bind:value
		rows={3}
		placeholder="Allergies, special requests…"
		class="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
	></textarea>
</section>
