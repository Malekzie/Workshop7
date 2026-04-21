<script>
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Skeleton placeholder grid while menu catalog fetch is in flight.

	import { Skeleton } from '$lib/components/ui/skeleton';

	let { count = 6 } = $props();
</script>

<div class="grid grid-cols-2 gap-3 sm:gap-5 lg:grid-cols-3">
	{#each Array.from({ length: count }), i (i)}
		<div class="flex flex-col overflow-hidden rounded-xl border border-border bg-card">
			<Skeleton class="h-48 w-full rounded-none" />
			<div class="flex flex-col gap-3 p-4">
				<Skeleton class="h-4 w-3/4 rounded" />
				<Skeleton class="h-3 w-full rounded" />
				<Skeleton class="h-3 w-2/3 rounded" />
				<Skeleton class="h-5 w-16 rounded" />
				<Skeleton class="h-8 w-full rounded-full" />
			</div>
		</div>
	{/each}
</div>
