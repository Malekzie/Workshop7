<script>
	import { X } from '@lucide/svelte';

	let { resultCount = 0, activeTagName = null, searchQuery = '', onClear = () => {} } = $props();
</script>

{#if activeTagName || searchQuery}
	<div class="mb-5 flex items-center gap-2 text-sm text-muted-foreground">
		<span>
			{resultCount} result{resultCount !== 1 ? 's' : ''}
			{#if activeTagName}
				in <span class="font-medium text-foreground">{activeTagName}</span>{/if}
			{#if searchQuery}
				for <span class="font-medium text-foreground">"{searchQuery}"</span>{/if}
		</span>
		<button
			onclick={onClear}
			class="ml-1 flex items-center gap-1 rounded-full border border-border px-2 py-0.5 text-xs hover:bg-muted"
		>
			<X class="h-3 w-3" /> Clear
		</button>
	</div>
{/if}
