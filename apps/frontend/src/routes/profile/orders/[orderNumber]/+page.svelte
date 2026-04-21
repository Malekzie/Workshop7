<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Thin route: passes the orderNumber param through to OrderTracking for authenticated lookup.

	import { page } from '$app/state';
	import OrderTracking from '$lib/components/orders/OrderTracking.svelte';
</script>

<OrderTracking orderNumber={page.params.orderNumber ?? ''} />
