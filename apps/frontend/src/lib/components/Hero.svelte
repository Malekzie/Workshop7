<script lang="ts">
  // No props needed for static mockup
</script>

<section class="relative overflow-hidden bg-background">
  <!-- Warm texture overlay using CSS gradient -->
  <div class="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,_oklch(0.85_0.05_60)_0%,_transparent_60%)] pointer-events-none"></div>
  <div class="absolute inset-0 bg-[radial-gradient(ellipse_at_bottom_left,_oklch(0.90_0.03_145)_0%,_transparent_50%)] pointer-events-none opacity-40"></div>

  <div class="relative mx-auto max-w-7xl px-6 py-28 md:py-40 grid md:grid-cols-2 gap-12 items-center">

    <!-- Text content -->
    <div class="flex flex-col gap-6">
      <p class="text-sm font-semibold uppercase tracking-widest text-primary">
        Fresh • Artisan • Local
      </p>
      <h1 class="font-serif text-5xl md:text-6xl lg:text-7xl font-bold text-foreground leading-tight">
        Baked with<br />heart.
      </h1>
      <p class="text-lg text-muted-foreground max-w-md leading-relaxed">
        Every loaf, pastry, and cake at Peelin' Good is made from scratch — with ingredients you can trust and flavors you'll remember.
      </p>
      <div class="flex gap-4 pt-2">
        <a
          href="#specials"
          class="inline-flex items-center rounded-full bg-primary px-6 py-3 text-sm font-semibold text-primary-foreground hover:opacity-90 transition-opacity"
        >
          Browse Menu
        </a>
        <a
          href="/about"
          class="inline-flex items-center rounded-full border border-border px-6 py-3 text-sm font-semibold text-foreground hover:bg-muted transition-colors"
        >
          Our Story
        </a>
      </div>
    </div>

    <!-- Decorative visual block (placeholder for image) -->
    <div class="relative hidden md:flex items-center justify-center">
      <div class="h-96 w-96 rounded-full bg-[oklch(0.92_0.04_60)] flex items-center justify-center shadow-inner">
        <div class="text-center">
          <p class="text-7xl">🥐</p>
          <p class="mt-4 font-serif text-xl text-foreground/60">est. 2024</p>
        </div>
      </div>
      <!-- Floating accent blobs -->
      <div class="absolute -top-4 -right-4 h-20 w-20 rounded-full bg-primary/20"></div>
      <div class="absolute -bottom-6 -left-6 h-14 w-14 rounded-full bg-accent/30"></div>
    </div>

  </div>
</section>
