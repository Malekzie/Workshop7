<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Guest contact capture with FormValidationUtil for email and phone before order submit.

	import { FormValidationUtil } from '$lib/utils/formValidation';

	let {
		firstName = $bindable(''),
		lastName = $bindable(''),
		email = $bindable(''),
		phone = $bindable('')
	}: {
		firstName?: string;
		lastName?: string;
		email?: string;
		phone?: string;
	} = $props();
</script>

<section class="rounded-xl border border-border bg-card p-6 shadow-sm">
	<h2 class="mb-4 text-lg font-semibold text-foreground">Contact</h2>
	<div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
		<div class="flex flex-col gap-1">
			<label for="firstName" class="text-sm font-medium text-foreground">
				First Name <span class="text-destructive">*</span>
			</label>
			<input
				id="firstName"
				type="text"
				bind:value={firstName}
				required
				class="rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
			/>
		</div>
		<div class="flex flex-col gap-1">
			<label for="lastName" class="text-sm font-medium text-foreground">
				Last Name <span class="text-destructive">*</span>
			</label>
			<input
				id="lastName"
				type="text"
				bind:value={lastName}
				required
				class="rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
			/>
		</div>
		<div class="flex flex-col gap-1 sm:col-span-2">
			<label for="email" class="text-sm font-medium text-foreground">
				Email <span class="text-destructive">*</span>
			</label>
			<input
				id="email"
				type="email"
				bind:value={email}
				required
				class="rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
			/>
		</div>
		<div class="flex flex-col gap-1 sm:col-span-2">
			<label for="phone" class="text-sm font-medium text-foreground">Phone</label>
			<input
				id="phone"
				type="tel"
				bind:value={phone}
				oninput={(e) => {
					phone = FormValidationUtil.formatPhone(e.currentTarget.value);
				}}
				class="rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
			/>
		</div>
	</div>
</section>
