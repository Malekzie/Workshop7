<script>
	let { activeTagId = $bindable(null), tags = [] } = $props();
</script>

<aside class="hidden w-52 shrink-0 md:block">
	<div class="sticky top-6 px-4 py-8">
		<p class="mb-3 text-[10px] font-bold tracking-widest text-muted-foreground uppercase">
			Categories
		</p>
		<div class="flex flex-col gap-0.5">
			<button
				onclick={() => (activeTagId = null)}
				class="rounded-lg px-3 py-2 text-left text-sm font-medium transition-colors {activeTagId ===
				null
					? 'bg-primary text-primary-foreground'
					: 'text-foreground/70 hover:bg-muted hover:text-foreground'}"
			>
				All items
			</button>
			{#each tags as tag (tag.id)}
				<button
					onclick={() => (activeTagId = tag.id)}
					class="rounded-lg px-3 py-2 text-left text-sm font-medium transition-colors {activeTagId ===
					tag.id
						? 'bg-primary text-primary-foreground'
						: 'text-foreground/70 hover:bg-muted hover:text-foreground'}"
				>
					{tag.name}
				</button>
			{/each}
		</div>
	</div>
</aside>
