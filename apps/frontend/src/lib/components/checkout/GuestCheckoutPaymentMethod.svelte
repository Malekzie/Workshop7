<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Guest tender choice including cash versus card types that map to CheckoutRequest payment enums.

	let {
		paymentMethod = $bindable<'cash' | 'credit_card' | 'debit_card' | 'online'>('cash')
	}: {
		paymentMethod?: 'cash' | 'credit_card' | 'debit_card' | 'online';
	} = $props();

	const methods: [string, string][] = [
		['cash', 'Cash'],
		['credit_card', 'Credit Card'],
		['debit_card', 'Debit Card'],
		['online', 'Online']
	];
</script>

<section class="rounded-xl border border-border bg-card p-6 shadow-sm">
	<h2 class="mb-4 text-lg font-semibold text-foreground">Payment</h2>
	<div class="grid grid-cols-2 gap-3 sm:grid-cols-4">
		{#each methods as [val, label] (val)}
			<label
				class="flex cursor-pointer items-center gap-2 rounded-lg border border-border px-3 py-2 text-sm transition-colors hover:bg-muted {paymentMethod ===
				val
					? 'border-primary bg-muted'
					: ''}"
			>
				<input type="radio" bind:group={paymentMethod} value={val} class="accent-primary" />
				{label}
			</label>
		{/each}
	</div>
</section>
