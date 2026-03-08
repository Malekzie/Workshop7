<script lang="ts">
  const categories = [
    { name: 'Breads', emoji: '🍞', description: 'Sourdoughs, loaves & rolls', href: '/menu/breads' },
    { name: 'Pastries', emoji: '🥐', description: 'Croissants, danishes & tarts', href: '/menu/pastries' },
    { name: 'Cakes', emoji: '🎂', description: 'Slices, custom & celebration', href: '/menu/cakes' },
    { name: 'Seasonal', emoji: '🍋', description: 'Limited time favourites', href: '/menu/seasonal' }
  ];
</script>

<section class="bg-background py-14 px-6">
  <div class="mx-auto max-w-7xl">

    <div class="mb-8 flex flex-col gap-1">
      <p class="text-sm font-semibold uppercase tracking-widest text-primary">Browse</p>
      <h2 class="font-serif text-3xl font-bold text-foreground">What are you craving?</h2>
    </div>

    <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
      {#each categories as cat}
        <a
          href={cat.href}
          class="group flex flex-col items-center gap-3 rounded-2xl border border-border bg-card p-6 text-center hover:border-primary/40 hover:shadow-md transition-all duration-200"
        >
          <span class="text-5xl group-hover:scale-110 transition-transform duration-200">{cat.emoji}</span>
          <div>
            <p class="font-serif text-base font-semibold text-foreground">{cat.name}</p>
            <p class="text-xs text-muted-foreground mt-0.5 leading-snug">{cat.description}</p>
          </div>
        </a>
      {/each}
    </div>

  </div>
</section>
