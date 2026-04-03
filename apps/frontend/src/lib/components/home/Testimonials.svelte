<script lang="ts">
	const testimonials = [
		{
			quote:
				"The sourdough here is the best I've had outside of San Francisco. I pick one up every Saturday morning without fail.",
			name: 'Sarah M.',
			detail: 'Regular since opening day'
		},
		{
			quote:
				'Ordered a custom birthday cake last minute and they pulled it off beautifully. Tasted even better than it looked.',
			name: 'James R.',
			detail: 'Custom cake order'
		},
		{
			quote:
				"The croissants are dangerously good. Perfectly flaky, buttery — I've already converted three coworkers.",
			name: 'Priya K.',
			detail: 'Pastry enthusiast'
		}
	];

	function stars(n: number) {
		return Array(n).fill('★').join('');
	}
</script>

<section class="bg-muted px-6 py-20">
	<div class="mx-auto max-w-7xl">
		<div class="mb-10 flex flex-col gap-2">
			<p class="text-sm font-semibold tracking-widest text-primary uppercase">Kind Words</p>
			<h2 class="font-serif text-4xl font-bold text-foreground">What our customers say</h2>
		</div>

		<div class="grid grid-cols-1 gap-6 md:grid-cols-3">
			{#each testimonials as t (t)}
				<figure class="flex flex-col gap-4 rounded-2xl border border-border bg-card p-6">
					<p class="text-sm tracking-widest text-primary">{stars(5)}</p>
					<blockquote class="flex-1 text-sm leading-relaxed text-foreground">
						"{t.quote}"
					</blockquote>
					<figcaption class="border-t border-border pt-4">
						<p class="text-sm font-semibold text-foreground">{t.name}</p>
						<p class="text-xs text-muted-foreground">{t.detail}</p>
					</figcaption>
				</figure>
			{/each}
		</div>
	</div>
</section>
