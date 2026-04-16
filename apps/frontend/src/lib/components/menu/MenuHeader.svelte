<script>
	import { Input } from '$lib/components/ui/input';
	import { Search, X } from '@lucide/svelte';

	let { activeTagId = $bindable(null), searchQuery = $bindable(''), tags = [] } = $props();
</script>

<header class="border-b border-border/60 bg-background px-6 pt-14 pb-10 text-center">
	<p class="mb-3 text-[11px] font-semibold tracking-[0.2em] text-primary uppercase">
		Peelin' Good Bakery
	</p>
	<h1 class="text-5xl font-black tracking-tight text-foreground sm:text-6xl">Our Menu</h1>
	<p class="mt-3 text-sm text-muted-foreground">Fresh from the oven, crafted with care</p>

	<div class="relative mx-auto mt-8 max-w-md">
		<Search
			class="pointer-events-none absolute top-1/2 left-3.5 h-4 w-4 -translate-y-1/2 text-muted-foreground"
		/>
		<Input
			type="text"
			placeholder="Search breads, pastries, cakes..."
			bind:value={searchQuery}
			class="rounded-full bg-card pr-10 pl-10 shadow-sm focus-visible:ring-ring"
		/>
		{#if searchQuery}
			<button
				onclick={() => (searchQuery = '')}
				class="absolute top-1/2 right-3.5 -translate-y-1/2 text-muted-foreground hover:text-foreground"
				aria-label="Clear search"
			>
				<X class="h-4 w-4" />
			</button>
		{/if}
	</div>

	<div class="mt-4 flex gap-2 overflow-x-auto pb-1 md:hidden" style="scrollbar-width: none;">
		<button
			onclick={() => (activeTagId = null)}
			class="shrink-0 rounded-full px-4 py-1.5 text-xs font-semibold transition-colors {activeTagId ===
			null
				? 'bg-primary text-primary-foreground'
				: 'border border-border bg-card text-foreground/70 hover:text-foreground'}"
		>
			All
		</button>
		{#each tags as tag (tag.id)}
			<button
				onclick={() => (activeTagId = tag.id)}
				class="shrink-0 rounded-full px-4 py-1.5 text-xs font-semibold transition-colors {activeTagId ===
				tag.id
					? 'bg-primary text-primary-foreground'
					: 'border border-border bg-card text-foreground/70 hover:text-foreground'}"
			>
				{tag.name}
			</button>
		{/each}
	</div>
</header>
