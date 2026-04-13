<script lang="ts">
	import { resolve } from '$app/paths';
	import { goto } from '$app/navigation';
	import { cart } from '$lib/stores/cart';
	import { onMount } from 'svelte';
	import { get } from 'svelte/store';
	import { isLoggedIn } from '$lib/stores/authStore';
	import { api } from '$lib/utils/apiClient';
	import { formatDiscountCad, formatPriceCad } from '$lib/utils/money';
	import {
		validateField,
		generateTimeSlots,
		sortedBakeries,
		asapScheduleDate,
		type BakeryAddress,
		type Bakery,
		type BakeryHour,
		type SavedAddress,
		type CustomerProfile,
		type ErrorKey
	} from '$lib/services/checkout';
	import CheckoutGuestContact from '$lib/components/checkout/CheckoutGuestContact.svelte';
	import CheckoutDeliveryAddress from '$lib/components/checkout/CheckoutDeliveryAddress.svelte';
	import CheckoutFulfillment from '$lib/components/checkout/CheckoutFulfillment.svelte';
	import CheckoutSchedule from '$lib/components/checkout/CheckoutSchedule.svelte';
	import CheckoutSummary from '$lib/components/checkout/CheckoutSummary.svelte';
	import CheckoutPayment from '$lib/components/checkout/CheckoutPayment.svelte';

	// ── Session ──────────────────────────────────────────────────────────────────

	const isGuest = !get(isLoggedIn);

	// ── Page state ───────────────────────────────────────────────────────────────

	let pageLoading = $state(true);
	let pageError = $state('');

	// Guest contact
	let guestFirstName = $state('');
	let guestLastName = $state('');
	let guestEmail = $state('');
	let guestPhone = $state('');

	// Delivery address (for guest or logged-in customer without saved address)
	let deliveryLine1 = $state('');
	let deliveryLine2 = $state('');
	let deliveryCity = $state('');
	let deliveryProvince = $state('AB');
	let deliveryPostal = $state('');

	// ── Validation ───────────────────────────────────────────────────────────

	let errors = $state<Record<ErrorKey, string>>({
		guestFirstName: '',
		guestLastName: '',
		guestEmail: '',
		guestPhone: '',
		deliveryLine1: '',
		deliveryLine2: '',
		deliveryCity: '',
		deliveryProvince: '',
		deliveryPostal: ''
	});

	let touched = $state<Record<ErrorKey, boolean>>({
		guestFirstName: false,
		guestLastName: false,
		guestEmail: false,
		guestPhone: false,
		deliveryLine1: false,
		deliveryLine2: false,
		deliveryCity: false,
		deliveryProvince: false,
		deliveryPostal: false
	});

	function handleBlur(name: ErrorKey) {
		touched[name] = true;
		errors[name] = validateField(name, {
			guestFirstName,
			guestLastName,
			guestEmail,
			guestPhone,
			deliveryLine1,
			deliveryLine2,
			deliveryCity,
			deliveryProvince,
			deliveryPostal
		});
	}

	function handleInput(name: ErrorKey) {
		if (touched[name]) {
			errors[name] = validateField(name, {
				guestFirstName,
				guestLastName,
				guestEmail,
				guestPhone,
				deliveryLine1,
				deliveryLine2,
				deliveryCity,
				deliveryProvince,
				deliveryPostal
			});
		}
	}

	function validateContactFields(): boolean {
		if (!isGuest) return true;
		const fields: ErrorKey[] = ['guestFirstName', 'guestLastName', 'guestEmail', 'guestPhone'];
		fields.forEach((f) => {
			touched[f] = true;
			errors[f] = validateField(f, {
				guestFirstName,
				guestLastName,
				guestEmail,
				guestPhone,
				deliveryLine1,
				deliveryLine2,
				deliveryCity,
				deliveryProvince,
				deliveryPostal
			});
		});
		return !fields.some((f) => errors[f]);
	}

	function validateDeliveryFields(): boolean {
		if (!needsDeliveryForm) return true;
		const fields: ErrorKey[] = [
			'deliveryLine1',
			'deliveryCity',
			'deliveryProvince',
			'deliveryPostal'
		];
		fields.forEach((f) => {
			touched[f] = true;
			errors[f] = validateField(f, {
				guestFirstName,
				guestLastName,
				guestEmail,
				guestPhone,
				deliveryLine1,
				deliveryLine2,
				deliveryCity,
				deliveryProvince,
				deliveryPostal
			});
		});
		return !fields.some((f) => errors[f]);
	}

	// Loaded data
	let customer = $state<CustomerProfile | null>(null);
	let bakeries = $state<Bakery[]>([]);
	let selectedBakeryId = $state<number | null>(null);
	let stripePublishableKey = $state('');

	// User geolocation
	let userLat = $state<number | null>(null);
	let userLng = $state<number | null>(null);

	// Fulfillment
	let orderMethod = $state<'pickup' | 'delivery'>('pickup');

	// Schedule
	let scheduleEnabled = $state(false);
	let scheduleDate = $state('');
	let scheduleTime = $state('');
	let bakeryHours = $state<BakeryHour[]>([]);
	let bakeryHoursLoading = $state(false);
	let availableTimeSlots = $state<string[]>([]);

	// Custom delivery address override (logged-in customers)
	let useCustomAddress = $state(false);

	// Misc
	let orderComment = $state('');

	// Submit
	let submitting = $state(false);
	let submitError = $state('');

	// Payment phase
	let phase = $state<'form' | 'payment'>('form');
	let pendingOrderId = $state('');
	let pendingOrderNumber = $state('');
	let pendingClientSecret = $state('');
	let pendingPaymentIntentId = $state('');
	let pendingSubtotal = $state<number | null>(null);
	let pendingDiscount = $state<number | null>(null);
	let pendingDeliveryFee = $state<number | null>(null);
	let pendingTaxAmount = $state<number | null>(null);
	let pendingGrandTotal = $state<number | null>(null);
	let paymentContainer = $state<HTMLDivElement | undefined>(undefined);
	let stripePaymentLoading = $state(false);
	let paymentError = $state('');

	// ── Derived ──────────────────────────────────────────────────────────────────

	const DELIVERY_FEE = 7;
	const DELIVERY_FREE_THRESHOLD = 50;

	const selectedBakery = $derived(bakeries.find((b) => b.id === selectedBakeryId) ?? null);

	const deliveryFee = $derived(
		orderMethod === 'delivery' && $cart.subtotal < DELIVERY_FREE_THRESHOLD ? DELIVERY_FEE : 0
	);

	const needsDeliveryForm = $derived(
		orderMethod === 'delivery' && (isGuest || !customer?.addressId || useCustomAddress)
	);

	const hasScheduledAt = $derived(scheduleEnabled && !!scheduleDate && !!scheduleTime);

	const scheduledAtIso = $derived(hasScheduledAt ? `${scheduleDate}T${scheduleTime}:00Z` : null);

	// ── Lifecycle ────────────────────────────────────────────────────────────────

	onMount(async () => {
		if ($cart.items.length === 0) {
			goto(resolve('/'));
			return;
		}

		// Request geolocation — silently ignore if denied
		if (typeof navigator !== 'undefined' && navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(
				(pos) => {
					userLat = pos.coords.latitude;
					userLng = pos.coords.longitude;
				},
				() => {}
			);
		}

		try {
			const [bakeriesData, stripeConfig] = await Promise.all([
				api.get<Bakery[]>('/bakeries'),
				api.get<{ publishableKey: string }>('/stripe/config').catch(() => ({ publishableKey: '' }))
			]);
			stripePublishableKey = stripeConfig.publishableKey ?? '';
			bakeries = bakeriesData;
			if (bakeries.length > 0) selectedBakeryId = bakeries[0].id;

			if (!isGuest) {
				customer = await api.get<CustomerProfile>('/customers/me');
			}
		} catch (err) {
			pageError = err instanceof Error ? err.message : 'Failed to load checkout data.';
		} finally {
			pageLoading = false;
		}
	});

	// Re-sort bakeries when geolocation updates
	$effect(() => {
		if (userLat !== null && userLng !== null && bakeries.length > 0) {
			const sorted = sortedBakeries(bakeries, userLat, userLng);
			bakeries = sorted;
			if (!selectedBakeryId) {
				selectedBakeryId = sorted[0]?.id ?? null;
			}
		}
	});

	// Fetch bakery hours whenever the selected bakery changes
	$effect(() => {
		if (!selectedBakeryId) return;
		const id = selectedBakeryId;
		bakeryHours = [];
		bakeryHoursLoading = true;
		api
			.get<BakeryHour[]>(`/bakeries/${id}/hours`)
			.then((h) => {
				bakeryHours = h;
			})
			.catch(() => {
				bakeryHours = [];
			})
			.finally(() => {
				bakeryHoursLoading = false;
			});
	});

	// Rebuild time slots when date or hours change (30-minute intervals)
	$effect(() => {
		const slots = generateTimeSlots(scheduleDate, bakeryHours);
		availableTimeSlots = slots;
		if (slots.length > 0 && (!scheduleTime || !slots.includes(scheduleTime))) {
			scheduleTime = slots[0];
		} else if (slots.length === 0) {
			scheduleTime = '';
		}
	});

	// Auto-fill date when "Schedule for later" is enabled; reset when disabled
	$effect(() => {
		if (!scheduleEnabled) {
			scheduleDate = '';
			scheduleTime = '';
			return;
		}
		// Wait until bakery hours have loaded before setting the default date so
		// the slot-building effect can immediately validate it
		if (!bakeryHours.length) return;
		// Only set the default if the user hasn't already picked a date
		if (!scheduleDate) {
			scheduleDate = asapScheduleDate();
		}
	});

	// ── Submit ───────────────────────────────────────────────────────────────────

	async function confirmOrder() {
		submitError = '';

		if ($cart.items.length === 0) {
			submitError = 'Your cart is empty.';
			return;
		}
		if (!selectedBakeryId) {
			submitError = 'Please select a bakery location.';
			return;
		}

		const contactOk = validateContactFields();
		const deliveryOk = validateDeliveryFields();
		if (!contactOk || !deliveryOk) return;

		submitting = true;
		try {
			type InlineAddress = {
				line1: string;
				line2?: string;
				city: string;
				province: string;
				postalCode: string;
			};
			type CheckoutBody = {
				bakeryId: number;
				orderMethod: string;
				paymentMethod: string;
				comment?: string;
				scheduledAt?: string;
				addressId?: number;
				deliveryAddress?: InlineAddress;
				items: { productId: number; quantity: number }[];
				guest?: {
					firstName?: string;
					lastName?: string;
					email?: string;
					phone?: string;
					addressLine1?: string;
					addressLine2?: string;
					city?: string;
					province?: string;
					postalCode?: string;
				};
			};

			const body: CheckoutBody = {
				bakeryId: selectedBakeryId,
				orderMethod,
				paymentMethod: 'online',
				comment: orderComment || undefined,
				scheduledAt: scheduledAtIso ?? undefined,
				items: $cart.items.map((i) => ({ productId: i.productId, quantity: i.quantity }))
			};

			if (orderMethod === 'delivery') {
				if (isGuest) {
					// Guest: address goes in the guest object
				} else if (useCustomAddress) {
					body.deliveryAddress = {
						line1: deliveryLine1,
						line2: deliveryLine2 || undefined,
						city: deliveryCity,
						province: deliveryProvince,
						postalCode: deliveryPostal
					};
				} else if (customer?.addressId) {
					body.addressId = customer.addressId;
				}
			}

			if (isGuest) {
				body.guest = {
					firstName: guestFirstName || undefined,
					lastName: guestLastName || undefined,
					email: guestEmail || undefined,
					phone: guestPhone || undefined,
					...(orderMethod === 'delivery'
						? {
								addressLine1: deliveryLine1,
								addressLine2: deliveryLine2 || undefined,
								city: deliveryCity,
								province: deliveryProvince,
								postalCode: deliveryPostal
							}
						: {})
				};
			}

			const session = await api.post<{
				orderId: string;
				orderNumber: string;
				clientSecret: string;
				paymentIntentId: string;
				subtotal?: number;
				discount?: number;
				deliveryFee?: number;
				taxAmount?: number;
				grandTotal?: number;
			}>('/orders', body);

			pendingOrderId = session.orderId;
			pendingOrderNumber = session.orderNumber;
			pendingClientSecret = session.clientSecret;
			pendingPaymentIntentId = session.paymentIntentId;
			pendingSubtotal = session.subtotal ?? null;
			pendingDiscount = session.discount ?? null;
			pendingDeliveryFee = session.deliveryFee ?? null;
			pendingTaxAmount = session.taxAmount ?? null;
			pendingGrandTotal = session.grandTotal ?? null;

			// Stripe must be configured to process payments
			if (!stripePublishableKey) {
				submitError = 'Payment processing is not available. Stripe is not configured.';
				return;
			}

			phase = 'payment';
			// Give Svelte a tick to render the payment container before mounting
			setTimeout(mountStripeElement, 50);
		} catch (err) {
			submitError = err instanceof Error ? err.message : 'Unexpected error. Please try again.';
		} finally {
			submitting = false;
		}
	}

	// ── Stripe ───────────────────────────────────────────────────────────────────

	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	let stripeInstance: any = null;
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	let stripeElements: any = null;

	async function mountStripeElement() {
		if (!paymentContainer || !stripePublishableKey || !pendingClientSecret) return;
		stripePaymentLoading = true;

		// Dynamically load Stripe.js if not already present
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		if (!(window as any).Stripe) {
			await new Promise<void>((res, rej) => {
				const s = document.createElement('script');
				s.src = 'https://js.stripe.com/v3/';
				s.onload = () => res();
				s.onerror = () => rej(new Error('Failed to load Stripe.js'));
				document.head.appendChild(s);
			});
		}

		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		stripeInstance = (window as any).Stripe(stripePublishableKey);
		stripeElements = stripeInstance.elements({ clientSecret: pendingClientSecret });
		const paymentElement = stripeElements.create('payment');
		paymentElement.mount(paymentContainer);
		stripePaymentLoading = false;
	}

	async function pay() {
		if (!stripeInstance || !stripeElements) return;
		stripePaymentLoading = true;
		paymentError = '';

		const returnUrl = `${window.location.origin}/orders/${pendingOrderNumber}/confirmation`;

		const { error } = await stripeInstance.confirmPayment({
			elements: stripeElements,
			redirect: 'if_required',
			confirmParams: { return_url: returnUrl }
		});

		if (error) {
			paymentError = error.message ?? 'Payment failed. Please try again.';
			stripePaymentLoading = false;
			return;
		}

		// Notify the backend the payment succeeded (webhook may also do this, but belt-and-suspenders)
		try {
			await api.post(`/orders/${pendingOrderId}/confirm-stripe-payment`, {
				paymentIntentId: pendingPaymentIntentId
			});
		} catch (err) {
			// Surface fulfillment errors — webhook will also handle this if the request fails
			paymentError =
				err instanceof Error ? err.message : 'Could not confirm payment. Please contact support.';
			stripePaymentLoading = false;
			return;
		}

		cart.clear();
		goto(resolve(`/orders/${pendingOrderNumber}/confirmation`));
	}
</script>

<main class="mx-auto max-w-2xl px-6 py-16">
	<h1 class="mb-10 font-serif text-4xl font-bold text-foreground">Checkout</h1>

	{#if pageLoading}
		<p class="text-center text-muted-foreground">Loading…</p>
	{:else if pageError}
		<div
			class="rounded-xl border border-destructive bg-destructive/10 p-6 text-center text-destructive"
		>
			<p>{pageError}</p>
		</div>
	{:else if $cart.items.length === 0}
		<div class="rounded-xl border border-border bg-card p-8 text-center text-muted-foreground">
			<p>Your cart is empty.</p>
			<a href={resolve('/')} class="mt-4 inline-block text-primary hover:underline"
				>Go back to the menu</a
			>
		</div>
	{:else if phase === 'form'}
		<form
			onsubmit={(e) => {
				e.preventDefault();
				confirmOrder();
			}}
			class="flex flex-col gap-8"
		>
			<!-- ── Contact ─────────────────────────────────────────────────── -->
			{#if isGuest}
				<CheckoutGuestContact
					bind:firstName={guestFirstName}
					bind:lastName={guestLastName}
					bind:email={guestEmail}
					bind:phone={guestPhone}
					{errors}
					{touched}
					onBlur={handleBlur}
					onInput={handleInput}
					onPhoneInput={() => {}}
				/>
			{:else if customer}
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
						<a href={resolve('/profile')} class="text-xs text-primary hover:underline"
							>Edit profile</a
						>
					</div>
				</section>
			{/if}

			<!-- ── Fulfillment ─────────────────────────────────────────────── -->
			<CheckoutFulfillment
				{bakeries}
				bind:selectedBakeryId
				bind:orderMethod
				{userLat}
				{userLng}
				{isGuest}
				{customer}
				bind:useCustomAddress
				{deliveryLine1}
				{deliveryLine2}
				{deliveryCity}
				{deliveryProvince}
				{deliveryPostal}
				onBakeryChange={() => {}}
				onMethodChange={() => {}}
				onUseCustomAddress={() => {}}
				onUseSavedAddress={() => {
					deliveryLine1 = '';
					deliveryLine2 = '';
					deliveryCity = '';
					deliveryProvince = 'AB';
					deliveryPostal = '';
				}}
			/>

			<!-- ── Delivery Address ────────────────────────────────────────── -->
			{#if needsDeliveryForm}
				<section class="rounded-xl border border-border bg-card p-6 shadow-sm">
					<h2 class="mb-4 text-lg font-semibold text-foreground">Delivery Address</h2>
					<CheckoutDeliveryAddress
						bind:line1={deliveryLine1}
						bind:line2={deliveryLine2}
						bind:city={deliveryCity}
						bind:province={deliveryProvince}
						bind:postal={deliveryPostal}
						{errors}
						{touched}
						onBlur={handleBlur}
						onInput={handleInput}
						onPostalInput={() => {}}
					/>
				</section>
			{/if}

			<!-- ── Schedule ────────────────────────────────────────────────── -->
			<CheckoutSchedule
				bind:scheduleEnabled
				bind:scheduleDate
				bind:scheduleTime
				{bakeryHours}
				{bakeryHoursLoading}
				{availableTimeSlots}
				onScheduleToggle={() => {}}
				onDateChange={() => {}}
				onTimeChange={() => {}}
			/>

			<!-- ── Order Notes ─────────────────────────────────────────────── -->
			<section class="rounded-xl border border-border bg-card p-6 shadow-sm">
				<h2 class="mb-4 text-lg font-semibold text-foreground">Order Notes</h2>
				<textarea
					bind:value={orderComment}
					rows={3}
					placeholder="Allergies, special requests…"
					class="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground focus:ring-2 focus:ring-ring focus:outline-none"
				></textarea>
			</section>

			<!-- ── Summary ─────────────────────────────────────────────────── -->
			<CheckoutSummary
				items={$cart.items}
				subtotal={$cart.subtotal}
				{deliveryFee}
				{orderMethod}
				{customer}
			/>

			{#if submitError}
				<p
					class="rounded-lg border border-destructive bg-destructive/10 px-4 py-3 text-sm text-destructive"
				>
					{submitError}
				</p>
			{/if}

			<button
				type="submit"
				disabled={submitting}
				class="w-full rounded-lg bg-primary py-4 text-sm font-semibold text-primary-foreground transition-colors hover:opacity-90 disabled:opacity-60"
			>
				{submitting ? 'Processing…' : 'Confirm Order'}
			</button>
		</form>
	{:else}
		<!-- ── Payment phase ────────────────────────────────────────────── -->
		<CheckoutPayment
			orderNumber={pendingOrderNumber}
			subtotal={Number(pendingSubtotal ?? $cart.subtotal)}
			discount={pendingDiscount}
			deliveryFee={pendingDeliveryFee}
			taxAmount={pendingTaxAmount}
			grandTotal={pendingGrandTotal}
			{orderMethod}
			paymentLoading={stripePaymentLoading}
			{paymentError}
			bind:paymentContainer
			onPay={pay}
			onBack={() => (phase = 'form')}
		/>
	{/if}
</main>
