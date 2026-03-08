<script lang="ts">
  import { Wheat, Clock, MapPin, Star } from '@lucide/svelte';

  const stats = [
    { icon: Star, label: 'Est. 2024' },
    { icon: Wheat, label: '50+ Items Daily' },
    { icon: Clock, label: 'Baked Fresh Each Morning' },
    { icon: MapPin, label: 'Locally Sourced' }
  ];
</script>

<div class="border-y border-border bg-muted/60 py-4 px-6">
  <div class="mx-auto max-w-7xl flex flex-wrap items-center justify-center gap-x-10 gap-y-3">
    {#each stats as stat, i}
      <div class="flex items-center gap-2 text-sm text-muted-foreground">
        <stat.icon size={15} class="text-primary shrink-0" />
        <span class="font-medium">{stat.label}</span>
      </div>
      {#if i < stats.length - 1}
        <span class="hidden md:block text-border select-none">·</span>
      {/if}
    {/each}
  </div>
</div>
