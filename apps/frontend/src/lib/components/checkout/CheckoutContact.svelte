<script lang="ts">
	import { resolve } from '$app/paths';
	import type { CustomerProfile } from '$lib/services/checkout';

	let { customer }: { customer: CustomerProfile } = $props();
</script>

<section class="rounded-xl border border-border bg-card p-6 shadow-sm">
	<h2 class="mb-3 text-lg font-semibold text-foreground">Contact</h2>
	<div class="flex items-center justify-between">
		<div>
			{#if customer.firstName || customer.lastName}
				<p class="font-medium text-foreground">
					{[customer.firstName, customer.lastName].filter(Boolean).join(' ')}
				</p>
			{/if}
			{#if customer.email}
				<p class="text-sm text-muted-foreground">{customer.email}</p>
			{/if}
			{#if customer.phone}
				<p class="text-sm text-muted-foreground">{customer.phone}</p>
			{/if}
		</div>
		<a href={resolve('/profile')} class="text-xs text-primary hover:underline">Edit profile</a>
	</div>
</section>
