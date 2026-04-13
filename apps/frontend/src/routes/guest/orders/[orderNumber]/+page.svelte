<script lang="ts">
	import { page } from '$app/state';
	import OrderTracking from '$lib/components/orders/OrderTracking.svelte';
</script>

<OrderTracking orderNumber={page.params.orderNumber} />
