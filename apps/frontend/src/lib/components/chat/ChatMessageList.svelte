<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Scrollable message column for ChatMessage or StaffMessage rows with auto scroll on new items.

	import { tick } from 'svelte';
	import type { ChatMessage, StaffMessage } from '$lib/services/types';

	type Message = ChatMessage | StaffMessage;

	let {
		messages,
		currentUserId,
		typingLabel = ''
	}: {
		messages: Message[];
		currentUserId: string;
		typingLabel?: string;
	} = $props();

	let listEl: HTMLDivElement | undefined;

	$effect(() => {
		messages;
		typingLabel;
		tick().then(() => {
			if (listEl) listEl.scrollTop = listEl.scrollHeight;
		});
	});

	function formatTime(iso: string): string {
		return new Date(iso).toLocaleTimeString('en-CA', {
			hour: '2-digit',
			minute: '2-digit'
		});
	}

	function isMine(msg: Message): boolean {
		if (!currentUserId) return false;
		return String(msg.senderUserId).toLowerCase() === String(currentUserId).toLowerCase();
	}

	function isSystemMsg(msg: Message): boolean {
		const m = msg as ChatMessage;
		return Boolean(m.isSystem) || Boolean(m.staffOnly);
	}

	function isStaffOnly(msg: Message): boolean {
		return Boolean((msg as ChatMessage).staffOnly);
	}

	const sortedMessages = $derived(
		[...messages].sort(
			(a, b) => new Date(a.sentAt).getTime() - new Date(b.sentAt).getTime()
		)
	);

	function formatFull(iso: string): string {
		return new Date(iso).toLocaleString('en-CA', {
			month: 'short',
			day: 'numeric',
			hour: '2-digit',
			minute: '2-digit'
		});
	}
</script>

<div bind:this={listEl} class="flex flex-1 flex-col gap-2 overflow-y-auto p-3">
	{#each sortedMessages as msg (msg.id)}
		{@const sys = isSystemMsg(msg)}
		{@const audit = isStaffOnly(msg)}
		{@const mine = !sys && isMine(msg)}
		{#if sys}
			<div class="flex justify-center">
				<div
					class={[
						'max-w-[85%] rounded-full px-3 py-1 text-center text-[11px]',
						audit
							? 'border border-dashed border-[#8A9E7F]/50 bg-[#8A9E7F]/5 text-[#8A9E7F]'
							: 'bg-[#2C1A0E]/5 text-[#2C1A0E]/60 dark:bg-[#FAF7F2]/5 dark:text-[#FAF7F2]/60'
					]}
				>
					{#if audit}
						<span class="mr-1 font-semibold uppercase tracking-wide">Audit</span>
					{/if}
					{msg.text}
					<span class="ml-1 opacity-70">· {formatFull(msg.sentAt)}</span>
				</div>
			</div>
		{:else}
			<div class={['flex', mine ? 'justify-end' : 'justify-start']}>
				<div class="max-w-[75%]">
					<div
						class={[
							'rounded-2xl px-3 py-2 text-sm',
							mine && 'rounded-tr-sm bg-[#C4714A] text-white',
							!mine &&
								'rounded-tl-sm bg-card text-[#2C1A0E] shadow-sm dark:bg-[#FAF7F2]/10 dark:text-[#FAF7F2]'
						]}
					>
						{msg.text}
					</div>
					<p
						class={[
							'mt-0.5 px-1 text-[10px] text-[#2C1A0E]/40 dark:text-[#FAF7F2]/40',
							mine ? 'text-right' : 'text-left'
						]}
					>
						{formatTime(msg.sentAt)}
					</p>
				</div>
			</div>
		{/if}
	{/each}

	{#if typingLabel}
		<div class="flex justify-start">
			<div
				class="rounded-2xl rounded-tl-sm bg-card px-3 py-2 text-xs text-[#2C1A0E]/50 shadow-sm dark:bg-[#FAF7F2]/10 dark:text-[#FAF7F2]/50"
			>
				{typingLabel} is typing...
			</div>
		</div>
	{/if}

	{#if messages.length === 0 && !typingLabel}
		<div class="flex flex-1 items-center justify-center">
			<p class="text-xs text-[#2C1A0E]/40 dark:text-[#FAF7F2]/40">No messages yet</p>
		</div>
	{/if}
</div>
