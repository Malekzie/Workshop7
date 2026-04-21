// Contributor(s): Robbie
// Main: Robbie - SvelteKit fetch helpers for staff tools auth chat and shared API constants.

/**
 * WebSocket topic paths for chat. Keep in sync with ChatTopics.java on Android
 * and the topic chat thread strings configured on the Spring broker.
 */

const base = (threadId: number | string) => `/topic/chat/thread/${threadId}`;

export const ChatTopics = {
	messages: (threadId: number | string) => `${base(threadId)}/messages`,
	staffMessages: (threadId: number | string) => `${base(threadId)}/staff-messages`,
	typing: (threadId: number | string) => `${base(threadId)}/typing`,
	read: (threadId: number | string) => `${base(threadId)}/read`,
	status: (threadId: number | string) => `${base(threadId)}/status`,
	newThreads: () => '/topic/chat/threads',
	typingPublish: (threadId: number | string) => `/app/chat/thread/${threadId}/typing`
};
