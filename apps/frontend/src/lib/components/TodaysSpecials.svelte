<script lang="ts">
	import SpecialCard from './SpecialCard.svelte';

	const specials = [
		{
			name: 'Sourdough Boule',
			description: 'Classic tangy sourdough with a crispy crust and open crumb.',
			price: 8.5,
			emoji: '🍞',
			badge: "Today's Pick"
		},
		{
			name: 'Butter Croissant',
			description: 'Flaky, golden, and buttery — baked fresh every morning.',
			price: 3.75,
			emoji: '🥐'
		},
		{
			name: 'Lemon Tart',
			description: 'Silky lemon curd in a crisp pastry shell, dusted with powdered sugar.',
			price: 5.25,
			emoji: '🍋',
			badge: 'Seasonal'
		},
		{
			name: 'Cinnamon Roll',
			description: 'Soft, pillowy rolls with brown sugar filling and cream cheese glaze.',
			price: 4.5,
			emoji: '🌀'
		},
		{
			name: 'Carrot Cake Slice',
			description: 'Moist spiced cake with walnuts and whipped cream cheese frosting.',
			price: 6.0,
			emoji: '🎂'
		},
		{
			name: 'Almond Danish',
			description: 'Buttery pastry filled with almond cream and topped with sliced almonds.',
			price: 4.25,
			emoji: '🥮',
			badge: 'New'
		}
	];
</script>

<section id="specials" class="bg-background px-6 py-20">
	<div class="mx-auto max-w-7xl">
		<!-- Section header -->
		<div class="mb-10 flex flex-col gap-2">
			<p class="text-sm font-semibold tracking-widest text-primary uppercase">Fresh Today</p>
			<h2 class="font-serif text-4xl font-bold text-foreground">Today's Specials</h2>
			<p class="max-w-md text-muted-foreground">
				A curated selection of what's out of the oven right now.
			</p>
		</div>

		<!-- 3x2 grid -->
		<div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
			{#each specials as special (special)}
				<SpecialCard {...special} />
			{/each}
		</div>
	</div>
</section>
