<script>
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Staff index: immediate client redirect to the staff dashboard route.

	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { onMount } from 'svelte';

	onMount(() => {
		goto(resolve('/staff/dashboard'), { replaceState: true });
	});
</script>
