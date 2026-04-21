<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Static trust highlights row for the home page with icon and label pairs only.

	import { Wheat, Clock, MapPin, Star } from '@lucide/svelte';

	const items = [
		{ icon: Star, label: 'Est. 2026' },
		{ icon: Wheat, label: '50+ items daily' },
		{ icon: Clock, label: 'Baked fresh each morning' },
		{ icon: MapPin, label: 'Locally sourced' }
	];
</script>

<div class="border-y border-border bg-muted/40 px-6 py-3.5">
	<div class="mx-auto flex max-w-7xl flex-wrap items-center justify-center gap-x-10 gap-y-3">
		{#each items as item, i (i)}
			<div class="flex items-center gap-2 text-sm text-muted-foreground">
				<item.icon size={14} class="shrink-0 text-primary" />
				<span class="font-medium">{item.label}</span>
			</div>
			{#if i < items.length - 1}
				<span class="hidden text-border select-none md:block">·</span>
			{/if}
		{/each}
	</div>
</div>
