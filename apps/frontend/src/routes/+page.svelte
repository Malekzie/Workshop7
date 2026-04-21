<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Public home route: hero, specials, trust strip, categories, story, and testimonials sections.

	import Hero from '$lib/components/home/Hero.svelte';
	import TodaysSpecials from '$lib/components/home/TodaysSpecials.svelte';
	import TrustBar from '$lib/components/home/TrustBar.svelte';
	import CategoryStrip from '$lib/components/home/CategoryStrip.svelte';
	import OurStory from '$lib/components/home/OurStory.svelte';
	import Testimonials from '$lib/components/home/Testimonials.svelte';
</script>

<main>
	<Hero />
	<TodaysSpecials />
	<TrustBar />
	<CategoryStrip />
	<OurStory />
	<Testimonials />
</main>
