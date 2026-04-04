<script lang="ts">
	import { resolve } from '$app/paths';

	const categories = [
		{ name: 'Breads', emoji: '🍞', description: 'Sourdoughs, loaves & rolls' },
		{ name: 'Pastries', emoji: '🥐', description: 'Croissants, danishes & tarts' },
		{ name: 'Cakes', emoji: '🎂', description: 'Slices, custom & celebration' },
		{ name: 'Seasonal', emoji: '🍋', description: 'Limited time favourites' }
	];
</script>

<section class="bg-background px-6 py-14">
	<div class="mx-auto max-w-7xl">
		<div class="mb-8 flex flex-col gap-1">
			<p class="text-sm font-semibold tracking-widest text-primary uppercase">Browse</p>
			<h2 class="font-serif text-3xl font-bold text-foreground">What are you craving?</h2>
		</div>

		<div class="grid grid-cols-2 gap-4 md:grid-cols-4">
			{#each categories as cat (cat.name)}
				<a
					href={resolve(`/menu/${cat.name.toLowerCase()}`)}
					class="group flex flex-col items-center gap-3 rounded-2xl border border-border bg-card p-6 text-center transition-all duration-200 hover:border-primary/40 hover:shadow-md"
				>
					<span class="text-5xl transition-transform duration-200 group-hover:scale-110"
						>{cat.emoji}</span
					>
					<div>
						<p class="font-serif text-base font-semibold text-foreground">{cat.name}</p>
						<p class="mt-0.5 text-xs leading-snug text-muted-foreground">{cat.description}</p>
					</div>
				</a>
			{/each}
		</div>
	</div>
</section>
