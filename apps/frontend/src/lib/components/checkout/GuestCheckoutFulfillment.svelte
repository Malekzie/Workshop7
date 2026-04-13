<script lang="ts">
	import { formatCanadianPostalInput } from '$lib/utils/canadianPostalCode';

	let {
		orderMethod = $bindable<'pickup' | 'delivery'>('pickup'),
		line1 = $bindable(''),
		line2 = $bindable(''),
		city = $bindable(''),
		province = $bindable('AB'),
		postalCode = $bindable('')
	}: {
		orderMethod?: 'pickup' | 'delivery';
		line1?: string;
		line2?: string;
		city?: string;
		province?: string;
		postalCode?: string;
	} = $props();

	const provinces = [
		{ value: 'AB', label: 'Alberta' },
		{ value: 'BC', label: 'British Columbia' },
		{ value: 'MB', label: 'Manitoba' },
		{ value: 'NB', label: 'New Brunswick' },
		{ value: 'NL', label: 'Newfoundland and Labrador' },
		{ value: 'NS', label: 'Nova Scotia' },
		{ value: 'NT', label: 'Northwest Territories' },
		{ value: 'NU', label: 'Nunavut' },
		{ value: 'ON', label: 'Ontario' },
		{ value: 'PE', label: 'Prince Edward Island' },
		{ value: 'QC', label: 'Quebec' },
		{ value: 'SK', label: 'Saskatchewan' },
		{ value: 'YT', label: 'Yukon' }
	];
</script>

<section class="rounded-xl border border-border bg-card p-6 shadow-sm">
	<h2 class="mb-4 text-lg font-semibold text-foreground">Fulfillment</h2>
	<div class="flex gap-4">
		<label class="flex cursor-pointer items-center gap-2 text-sm">
			<input type="radio" bind:group={orderMethod} value="pickup" class="accent-primary" />
			Pickup
		</label>
		<label class="flex cursor-pointer items-center gap-2 text-sm">
			<input type="radio" bind:group={orderMethod} value="delivery" class="accent-primary" />
			Delivery
		</label>
	</div>

	{#if orderMethod === 'delivery'}
		<div class="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2">
			<div class="flex flex-col gap-1 sm:col-span-2">
				<label for="line1" class="text-sm font-medium text-foreground">
					Address Line 1 <span class="text-destructive">*</span>
				</label>
				<input
					id="line1"
					type="text"
					bind:value={line1}
					required
					class="rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
				/>
			</div>
			<div class="flex flex-col gap-1 sm:col-span-2">
				<label for="line2" class="text-sm font-medium text-foreground">Address Line 2</label>
				<input
					id="line2"
					type="text"
					bind:value={line2}
					class="rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
				/>
			</div>
			<div class="flex flex-col gap-1">
				<label for="city" class="text-sm font-medium text-foreground">
					City <span class="text-destructive">*</span>
				</label>
				<input
					id="city"
					type="text"
					bind:value={city}
					required
					class="rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
				/>
			</div>
			<div class="flex flex-col gap-1">
				<label for="province" class="text-sm font-medium text-foreground">
					Province <span class="text-destructive">*</span>
				</label>
				<select
					id="province"
					bind:value={province}
					class="rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
				>
					{#each provinces as p (p.value)}
						<option value={p.value}>{p.label}</option>
					{/each}
				</select>
			</div>
			<div class="flex flex-col gap-1">
				<label for="postalCode" class="text-sm font-medium text-foreground">
					Postal Code <span class="text-destructive">*</span>
				</label>
				<input
					id="postalCode"
					type="text"
					inputmode="text"
					autocomplete="postal-code"
					placeholder="A1A 1A1"
					maxlength="7"
					value={postalCode}
					oninput={(e) => {
						postalCode = formatCanadianPostalInput(e.currentTarget.value);
					}}
					required
					class="rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
				/>
			</div>
		</div>
	{/if}
</section>
