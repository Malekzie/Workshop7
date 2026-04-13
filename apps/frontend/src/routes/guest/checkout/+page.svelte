<script lang="ts">
	import { resolve } from '$app/paths';
	import { goto } from '$app/navigation';
	import { cart } from '$lib/stores/cart';
	import * as Sentry from '@sentry/sveltekit';
	import { FormValidationUtil } from '$lib/utils/formValidation';
	import { formatPriceCad } from '$lib/utils/money';
	import GuestCheckoutContact from '$lib/components/checkout/GuestCheckoutContact.svelte';
	import GuestCheckoutFulfillment from '$lib/components/checkout/GuestCheckoutFulfillment.svelte';
	import GuestCheckoutPaymentMethod from '$lib/components/checkout/GuestCheckoutPaymentMethod.svelte';
	import CheckoutOrderNotes from '$lib/components/checkout/CheckoutOrderNotes.svelte';

	const ORDERS_API = '/api/v1/orders';
	const BAKERY_ID = 1;

	function localDateYmd(): string {
		const d = new Date();
		return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
	}

	let firstName = $state('');
	let lastName = $state('');
	let email = $state('');
	let phone = $state('');
	let orderMethod = $state<'pickup' | 'delivery'>('pickup');
	let paymentMethod = $state<'cash' | 'credit_card' | 'debit_card' | 'online'>('cash');
	let orderComment = $state('');
	let line1 = $state('');
	let line2 = $state('');
	let city = $state('');
	let province = $state('AB');
	let postalCode = $state('');
	let submitting = $state(false);
	let error = $state('');

	async function submit() {
		error = '';

		if (!firstName.trim() || !lastName.trim()) {
			error = 'First and last name are required.';
			return;
		}
		if (!email.trim()) {
			error = 'Email is required.';
			return;
		}
		if (!FormValidationUtil.isValidEmail(email)) {
			error = 'Enter a valid email address.';
			return;
		}
		if (phone.trim() && !FormValidationUtil.isValidPhone(phone)) {
			error = 'Enter a valid phone number.';
			return;
		}
		if (orderMethod === 'delivery' && (!line1 || !city || !province || !postalCode)) {
			error = 'Full delivery address is required for delivery orders.';
			return;
		}
		if (orderMethod === 'delivery' && !FormValidationUtil.isValidCanadianPostalCode(postalCode)) {
			error = 'Enter a valid Canadian postal code (e.g. T2P 1J9).';
			return;
		}
		if ($cart.items.length === 0) {
			error = 'Your cart is empty.';
			return;
		}

		submitting = true;
		try {
			const body: Record<string, unknown> = {
				bakeryId: BAKERY_ID,
				orderMethod,
				paymentMethod,
				comment: orderComment.trim() || undefined,
				pricingLocalDate: localDateYmd(),
				guest: {
					firstName: firstName.trim(),
					lastName: lastName.trim(),
					email: email.trim(),
					phone: phone.trim() || undefined,
					...(orderMethod === 'delivery' && {
						addressLine1: line1.trim(),
						addressLine2: line2.trim() || undefined,
						city: city.trim(),
						province,
						postalCode: postalCode.trim().toUpperCase()
					})
				},
				items: $cart.items.map((i) => ({ productId: i.productId, quantity: i.quantity }))
			};

			const res = await fetch(ORDERS_API, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(body)
			});

			if (!res.ok) {
				const data = await res.json().catch(() => ({}));
				const msg =
					typeof data.message === 'string'
						? data.message
						: Array.isArray(data.errors)
							? data.errors
									.map((e: { defaultMessage?: string }) => e.defaultMessage)
									.filter(Boolean)
									.join(' ')
							: '';
				throw new Error(msg || `Server error ${res.status}`);
			}

			const session = (await res.json()) as { orderId?: string; orderNumber?: string };
			const orderNumber = session.orderNumber;
			if (!orderNumber) throw new Error('Order placed but no order number returned.');

			cart.clear();
			goto(resolve(`/guest/orders/${orderNumber}/confirmation`));
		} catch (err: unknown) {
			Sentry.withScope((scope) => {
				scope.setTag('action', 'GUEST_CHECKOUT_FAILED');
				scope.setTag('reason', 'api_error');
				Sentry.captureException(err instanceof Error ? err : new Error(String(err)));
			});
			error = err instanceof Error ? err.message : 'Unexpected error. Please try again.';
		} finally {
			submitting = false;
		}
	}
</script>

<main class="mx-auto max-w-2xl px-6 py-16">
	<div class="mb-10 flex items-center justify-between">
		<h1 class="font-serif text-4xl font-bold text-foreground">Guest Checkout</h1>
		<a href={resolve('/login?redirectTo=/checkout')} class="text-sm text-primary hover:underline">
			Sign in instead
		</a>
	</div>

	{#if $cart.items.length === 0}
		<div class="rounded-xl border border-border bg-card p-8 text-center text-muted-foreground">
			<p>Your cart is empty.</p>
			<a href={resolve('/')} class="mt-4 inline-block text-primary hover:underline">
				Go back to the menu
			</a>
		</div>
	{:else}
		<form
			onsubmit={(e) => {
				e.preventDefault();
				submit();
			}}
			class="flex flex-col gap-8"
		>
			<GuestCheckoutContact bind:firstName bind:lastName bind:email bind:phone />
			<GuestCheckoutFulfillment
				bind:orderMethod
				bind:line1
				bind:line2
				bind:city
				bind:province
				bind:postalCode
			/>
			<GuestCheckoutPaymentMethod bind:paymentMethod />
			<CheckoutOrderNotes bind:value={orderComment} />

			<section class="rounded-xl border border-border bg-card p-6 shadow-sm">
				<h2 class="mb-4 text-lg font-semibold text-foreground">Summary</h2>
				{#each $cart.items as item (item.productId)}
					<div class="flex justify-between py-1 text-sm text-muted-foreground">
						<span>{item.productName} × {item.quantity}</span>
						<span>{formatPriceCad(item.lineTotal)}</span>
					</div>
				{/each}
				<hr class="my-3 border-border" />
				<p class="text-xs text-muted-foreground">
					Line totals use menu prices. Tax and specials are applied when you confirm.
				</p>
				<div class="mt-3 flex justify-between text-sm text-muted-foreground">
					<span>Cart subtotal (list prices)</span>
					<span>{formatPriceCad($cart.subtotal)}</span>
				</div>
			</section>

			{#if error}
				<p
					class="rounded-lg border border-destructive bg-destructive/10 px-4 py-3 text-sm text-destructive"
				>
					{error}
				</p>
			{/if}

			<button
				type="submit"
				disabled={submitting}
				class="w-full rounded-lg bg-primary py-4 text-sm font-semibold text-primary-foreground transition-colors hover:opacity-90 disabled:opacity-60"
			>
				{submitting ? 'Placing Order…' : 'Place Order'}
			</button>
		</form>
	{/if}
</main>
