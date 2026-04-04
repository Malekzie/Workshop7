<script lang="ts">
	import { resolve } from '$app/paths';
</script>

<section class="bg-muted px-6 py-20">
	<div class="mx-auto grid max-w-7xl items-center gap-16 md:grid-cols-2">
		<!-- Text -->
		<div class="flex flex-col gap-6">
			<p class="text-sm font-semibold tracking-widest text-primary uppercase">Our Story</p>
			<h2 class="font-serif text-4xl leading-snug font-bold text-foreground">
				A little bakery with a lot of heart.
			</h2>
			<p class="leading-relaxed text-muted-foreground">
				Peelin' Good started from a simple belief: that good bread and a warm space can make any day
				better. We use locally sourced ingredients, time-honored techniques, and a whole lot of care
				to bring you baked goods that feel like home.
			</p>
			<p class="leading-relaxed text-muted-foreground">
				Every item on our menu is made by hand, in small batches, the same day you eat it. No
				shortcuts. Just honest baking.
			</p>
			<a
				href={resolve('/about')}
				class="inline-flex items-center self-start rounded-full border border-primary px-6 py-3 text-sm font-semibold text-primary transition-colors hover:bg-primary hover:text-primary-foreground"
			>
				Learn More
			</a>
		</div>

		<!-- Image placeholder -->
		<div class="relative">
			<div
				class="flex h-80 items-center justify-center overflow-hidden rounded-2xl bg-[oklch(0.90_0.04_60)] shadow-sm"
			>
				<div class="text-center">
					<p class="text-8xl">👩‍🍳</p>
					<p class="mt-4 font-serif text-lg text-foreground/50">Photo coming soon</p>
				</div>
			</div>
			<!-- Accent dot -->
			<div class="absolute -right-4 -bottom-4 -z-10 h-24 w-24 rounded-full bg-accent/20"></div>
		</div>
	</div>
</section>
