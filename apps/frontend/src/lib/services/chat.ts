// Contributor(s): Robbie
// Main: Robbie - SvelteKit fetch helpers for staff tools auth chat and shared API constants.

import { CHAT_API } from '$lib/services/constants';
import type { ChatMessage, ChatThread } from '$lib/services/types';

/** GET chat threads. Customers see at most one open row. Staff see the full open queue. */
export async function getThreads(category?: string): Promise<ChatThread[]> {
    const url = category ? `${CHAT_API}/threads?category=${encodeURIComponent(category)}` : `${CHAT_API}/threads`;
    const res = await fetch(url, { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to fetch threads');
    return res.json();
}

/** POST chat threads with JSON category for routing. */
export async function createThread(category: string): Promise<ChatThread> {
    const res = await fetch(`${CHAT_API}/threads`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ category })
    });
    if (!res.ok) throw new Error('Failed to create thread');
    return res.json();
}

/** GET thread messages for one thread id. */
export async function getMessages(threadId: number): Promise<ChatMessage[]> {
    const res = await fetch(`${CHAT_API}/threads/${threadId}/messages`, { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to fetch messages');
    return res.json();
}

/** POST thread messages. Body matches PostChatMessageRequest in OpenAPI. */
export async function postMessage(threadId: number, text: string): Promise<ChatMessage> {
    const res = await fetch(`${CHAT_API}/threads/${threadId}/messages`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ text })
    });
    if (!res.ok) throw new Error('Failed to post message');
    return res.json();
}

/** POST assign on a thread for the signed-in staff member. */
export async function assignThread(threadId: number): Promise<ChatThread> {
    const res = await fetch(`${CHAT_API}/threads/${threadId}/assign`, {
        method: 'POST',
        credentials: 'include'
    });
    if (!res.ok) throw new Error('Failed to assign thread');
    return res.json();
}

export async function closeThread(threadId: number): Promise<ChatThread> {
    const res = await fetch(`${CHAT_API}/threads/${threadId}/close`, {
        method: 'POST',
        credentials: 'include'
    });
    if (!res.ok) throw new Error('Failed to close thread');
    return res.json();
}

/** GET archived threads with optional category filter for admin audit lists. */
export async function getArchivedThreads(category?: string): Promise<ChatThread[]> {
    const url = category
        ? `${CHAT_API}/threads/archived?category=${encodeURIComponent(category)}`
        : `${CHAT_API}/threads/archived`;
    const res = await fetch(url, { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to fetch archived threads');
    return res.json();
}

/** POST read on a thread to clear unread state for the caller. */
export async function markThreadRead(threadId: number): Promise<void> {
    await fetch(`${CHAT_API}/threads/${threadId}/read`, {
        method: 'POST',
        credentials: 'include'
    });
}

/** POST transfer with JSON employeeUserId for staff reassignment. */
export async function transferThread(threadId: number, employeeUserId: string): Promise<ChatThread> {
    const res = await fetch(`${CHAT_API}/threads/${threadId}/transfer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ employeeUserId })
    });
    if (!res.ok) throw new Error('Failed to transfer thread');
    return res.json();
}

/** POST reopen for admin when a closed thread should return to open. */
export async function reopenThread(threadId: number): Promise<ChatThread> {
    const res = await fetch(`${CHAT_API}/threads/${threadId}/reopen`, {
        method: 'POST',
        credentials: 'include'
    });
    if (!res.ok) throw new Error('Failed to reopen thread');
    return res.json();
}
