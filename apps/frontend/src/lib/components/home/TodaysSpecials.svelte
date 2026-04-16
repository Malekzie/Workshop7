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

<section class="border-y border-border bg-muted/40 px-6 py-6">
	<div class="mx-auto flex max-w-7xl flex-wrap items-center gap-6">
		<div class="flex items-center gap-3">
			<span
				class="rounded-full bg-primary px-3 py-1 text-[10px] font-bold tracking-widest text-primary-foreground uppercase"
			>
				Today's special
			</span>
			<p class="text-xs font-semibold tracking-widest text-muted-foreground uppercase">
				Out of the oven
			</p>
		</div>

		{#if loading}
			<Skeleton class="h-16 flex-1 rounded-xl" />
		{:else if special}
			<div class="flex flex-1 flex-wrap items-center gap-4">
				{#if special.imageUrl}
					<img
						src={special.imageUrl}
						alt={special.name}
						class="h-16 w-16 rounded-xl object-cover"
					/>
				{/if}
				<div class="min-w-0 flex-1">
					<h3 class="truncate font-bold text-foreground">{special.name}</h3>
					<div class="flex items-baseline gap-2">
						<span class="text-lg font-bold text-primary">{formatPriceCad(finalPrice)}</span>
						{#if special.discountPercent}
							<span class="text-xs text-muted-foreground line-through">
								{formatPriceCad(special.basePrice)}
							</span>
							<span
								class="rounded-full bg-primary/10 px-2 py-0.5 text-xs font-semibold text-primary"
							>
								{special.discountPercent}% off
							</span>
						{/if}
					</div>
				</div>
				<div class="flex items-center gap-2">
					<button
						onclick={addToCart}
						class="flex items-center gap-1.5 rounded-full bg-primary px-4 py-2 text-xs font-bold tracking-wide text-primary-foreground transition
							{added ? 'bg-emerald-600' : 'hover:opacity-90'}"
					>
						{#if added}
							<Check class="h-3.5 w-3.5" />
							Added
						{:else}
							<ShoppingBag class="h-3.5 w-3.5" />
							Add to cart
						{/if}
					</button>
					<a
						href={resolve('/menu')}
						class="rounded-full border border-border px-4 py-2 text-xs font-semibold text-foreground transition hover:bg-background"
					>
						See menu
					</a>
				</div>
			</div>
		{:else}
			<p class="flex-1 text-sm text-muted-foreground">
				No special today — <a href={resolve('/menu')} class="text-primary hover:underline"
					>browse the full menu</a
				>.
			</p>
		{/if}
	</div>
</section>
