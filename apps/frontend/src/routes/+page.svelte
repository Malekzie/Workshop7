<script lang="ts">
  import Navbar from '$lib/components/Navbar.svelte';
  import Hero from '$lib/components/Hero.svelte';
  import TrustBar from '$lib/components/TrustBar.svelte';
  import CategoryStrip from '$lib/components/CategoryStrip.svelte';
  import TodaysSpecials from '$lib/components/TodaysSpecials.svelte';
  import OurStory from '$lib/components/OurStory.svelte';
  import Testimonials from '$lib/components/Testimonials.svelte';
  import Footer from '$lib/components/Footer.svelte';
</script>

<Navbar />
<main>
  <Hero />
  <TrustBar />
  <CategoryStrip />
  <TodaysSpecials />
  <OurStory />
  <Testimonials />
</main>
<Footer />
