<script>
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Small metric card used on staff dashboard and analytics for label value and optional subtext.

	let { label, value, subtext = null } = $props();
</script>

<div class="rounded-xl border border-border bg-card p-6 shadow-sm">
	<p class="text-xs font-semibold tracking-widest text-muted-foreground uppercase">{label}</p>
	<p class="mt-2 text-3xl font-bold text-foreground">{value}</p>
	{#if subtext}
		<p class="mt-1 text-xs text-muted-foreground">{subtext}</p>
	{/if}
</div>
