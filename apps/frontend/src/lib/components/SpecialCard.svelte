<script lang="ts">
  import { ShoppingCart } from '@lucide/svelte';

  interface Props {
    name: string;
    description: string;
    price: number;
    emoji: string; // placeholder until real images
    badge?: string;
  }

  let { name, description, price, emoji, badge }: Props = $props();
</script>

<article class="group relative flex flex-col rounded-2xl border border-border bg-card overflow-hidden hover:shadow-lg hover:border-primary/30 transition-all duration-200">

  <!-- Image area (placeholder) -->
  <div class="relative h-44 bg-[oklch(0.94_0.03_70)] flex items-center justify-center">
    <span class="text-6xl">{emoji}</span>
    {#if badge}
      <span class="absolute top-3 left-3 rounded-full bg-primary px-3 py-1 text-xs font-semibold text-primary-foreground">
        {badge}
      </span>
    {/if}
  </div>

  <!-- Content -->
  <div class="flex flex-1 flex-col gap-2 p-4">
    <h3 class="font-serif text-lg font-semibold text-foreground">{name}</h3>
    <p class="text-sm text-muted-foreground leading-snug flex-1">{description}</p>

    <div class="flex items-center justify-between pt-3 border-t border-border mt-2">
      <span class="text-base font-bold text-foreground">${price.toFixed(2)}</span>
      <button
        class="flex items-center gap-1.5 rounded-full bg-primary px-4 py-2 text-xs font-semibold text-primary-foreground hover:opacity-90 transition-opacity"
      >
        <ShoppingCart size={13} />
        Add to Order
      </button>
    </div>
  </div>
</article>
