<script lang="ts">
	import { resolve } from '$app/paths';
</script>

<section class="bg-[#FAF7F2] px-6 py-20">
	<div class="mx-auto grid max-w-7xl grid-cols-1 items-center gap-16 md:grid-cols-2">
		<!-- Image placeholder -->
		<div
			class="flex h-80 items-center justify-center overflow-hidden rounded-xl border border-border bg-[#F5EFE6]"
		>
			<p class="text-sm text-muted-foreground">Photo coming soon</p>
		</div>

		<!-- Text -->
		<div class="flex flex-col gap-4">
			<p class="text-[11px] font-semibold tracking-[0.2em] text-[#C4714A] uppercase">Our story</p>
			<h2 class="text-4xl leading-tight font-black tracking-tight text-[#2C1A0E]">
				A little bakery<br />with a lot of
				<span class="text-[#C4714A]">heart.</span>
			</h2>
			<p class="text-sm leading-relaxed text-muted-foreground">
				Peelin' Good started from a simple belief: good bread and a warm space can make any day
				better. We use locally sourced ingredients, time-honoured techniques, and a whole lot of
				care.
			</p>
			<p class="text-sm leading-relaxed text-muted-foreground">
				Every item is made by hand, in small batches, the same day you eat it. No shortcuts. Just
				honest baking.
			</p>
			<a
				href={resolve('/about')}
				class="mt-1 self-start text-sm font-semibold text-[#C4714A] underline decoration-[#C4714A]/40 underline-offset-4 hover:decoration-[#C4714A]"
			>
				Read the full story →
			</a>

			<!-- Stats -->
			<div class="mt-4 grid grid-cols-3 gap-4 border-t border-border pt-6">
				{#each [{ num: '50+', label: 'Items baked daily' }, { num: '3yr', label: 'Sourdough starter' }, { num: '100%', label: 'Local suppliers' }] as stat (stat.num)}
					<div>
						<p class="text-2xl font-black tracking-tight text-[#2C1A0E]">{stat.num}</p>
						<p class="mt-0.5 text-[11px] text-muted-foreground">{stat.label}</p>
					</div>
				{/each}
			</div>
		</div>
	</div>
</section>
