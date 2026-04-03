<script lang="ts">
	import { Wheat, Clock, MapPin, Star } from '@lucide/svelte';

	const stats = [
		{ icon: Star, label: 'Est. 2024' },
		{ icon: Wheat, label: '50+ Items Daily' },
		{ icon: Clock, label: 'Baked Fresh Each Morning' },
		{ icon: MapPin, label: 'Locally Sourced' }
	];
</script>

<div class="border-y border-border bg-muted/60 px-6 py-4">
	<div class="mx-auto flex max-w-7xl flex-wrap items-center justify-center gap-x-10 gap-y-3">
		{#each stats as stat, i (i)}
			<div class="flex items-center gap-2 text-sm text-muted-foreground">
				<stat.icon size={15} class="shrink-0 text-primary" />
				<span class="font-medium">{stat.label}</span>
			</div>
			{#if i < stats.length - 1}
				<span class="hidden text-border select-none md:block">·</span>
			{/if}
		{/each}
	</div>
</div>
