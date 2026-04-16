<script>
	import { onMount } from 'svelte';
	import { resolve } from '$app/paths';
	import { Skeleton } from '$lib/components/ui/skeleton';
	import { getTodaySpecial } from '$lib/services/product-specials';
	import { getProductById } from '$lib/services/products';
	import { formatPriceCad } from '$lib/utils/money';
	import { cart } from '$lib/stores/cart';
	import { ShoppingBag, Check } from '@lucide/svelte';

	let special = $state(null);
	let loading = $state(true);
	let added = $state(false);

	function localDateIso() {
		const d = new Date();
		const y = d.getFullYear();
		const m = String(d.getMonth() + 1).padStart(2, '0');
		const day = String(d.getDate()).padStart(2, '0');
		return `${y}-${m}-${day}`;
	}

	onMount(async () => {
		try {
			const today = await getTodaySpecial(localDateIso());
			const pid = today?.productId;
			if (pid == null) {
				special = null;
				return;
			}
			const product = await getProductById(pid);
			const pct = today.discountPercent != null ? Number(today.discountPercent) : null;
			special = {
				productId: product.id,
				name: product.name,
				description: product.description,
				basePrice: product.basePrice,
				discountPercent: pct,
				imageUrl: product.imageUrl
			};
		} catch {
			special = null;
		} finally {
			loading = false;
		}
	});

	const finalPrice = $derived(
		special
			? special.discountPercent
				? special.basePrice * (1 - special.discountPercent / 100)
				: special.basePrice
			: 0
	);

	function addToCart() {
		if (!special) return;
		cart.addItem({
			productId: special.productId,
			productName: special.name,
			productImageUrl: special.imageUrl ?? null,
			unitPrice: finalPrice,
			quantity: 1
		});
		added = true;
		setTimeout(() => (added = false), 1400);
	}
</script>

<section
	class="border-y-2 border-primary/30 bg-gradient-to-br from-primary/10 via-primary/5 to-background px-6 py-10"
>
	<div class="mx-auto max-w-5xl">
		<!-- Stacked header -->
		<div class="mb-6 text-center">
			<p class="text-[11px] font-bold tracking-[0.25em] text-primary uppercase">Out of the oven</p>
			<h2 class="mt-2 text-3xl font-black tracking-tight text-foreground lg:text-4xl">
				Today's Special
			</h2>
		</div>

		<!-- Elevated card -->
		<div
			class="rounded-2xl border border-border bg-card p-5 shadow-lg ring-1 ring-primary/10"
		>
			{#if loading}
				<Skeleton class="h-20 w-full rounded-xl" />
			{:else if special}
				<div class="flex flex-wrap items-center gap-5">
					{#if special.imageUrl}
						<img
							src={special.imageUrl}
							alt={special.name}
							class="h-20 w-20 rounded-xl object-cover shadow-sm sm:h-24 sm:w-24"
						/>
					{/if}
					<div class="min-w-0 flex-1">
						<h3 class="truncate text-lg font-bold text-foreground">{special.name}</h3>
						<div class="mt-1 flex flex-wrap items-baseline gap-2">
							<span class="text-2xl font-black text-primary">{formatPriceCad(finalPrice)}</span>
							{#if special.discountPercent}
								<span class="text-sm text-muted-foreground line-through">
									{formatPriceCad(special.basePrice)}
								</span>
								<span
									class="rounded-full bg-primary px-2.5 py-0.5 text-xs font-bold text-primary-foreground"
								>
									{special.discountPercent}% off
								</span>
							{/if}
						</div>
					</div>
					<div class="flex items-center gap-2">
						<button
							onclick={addToCart}
							class="flex items-center gap-1.5 rounded-full bg-primary px-5 py-2.5 text-sm font-bold text-primary-foreground shadow-md transition
								{added ? 'bg-emerald-600' : 'hover:opacity-90'}"
						>
							{#if added}
								<Check class="h-4 w-4" />
								Added
							{:else}
								<ShoppingBag class="h-4 w-4" />
								Add to cart
							{/if}
						</button>
						<a
							href={resolve('/menu')}
							class="rounded-full border border-border px-5 py-2.5 text-sm font-semibold text-foreground transition hover:bg-muted"
						>
							See menu
						</a>
					</div>
				</div>
			{:else}
				<p class="text-center text-sm text-muted-foreground">
					No special today — <a href={resolve('/menu')} class="text-primary hover:underline"
						>browse the full menu</a
					>.
				</p>
			{/if}
		</div>
	</div>
</section>
