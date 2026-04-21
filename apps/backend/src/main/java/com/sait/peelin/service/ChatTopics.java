// Contributor(s): Robbie
// Main: Robbie - STOMP topic path helpers for chat typing and message fan-out.

package com.sait.peelin.service;

/**
 * Canonical WebSocket topic paths for chat. Kept in sync with the TS + Kotlin
 * equivalents in the web and Android clients.
 */
public final class ChatTopics {

    private ChatTopics() {}

    public static final String NEW_THREADS = "/topic/chat/threads";

    private static String base(Integer threadId) {
        return "/topic/chat/thread/" + threadId;
    }

    public static String messages(Integer threadId) {
        return base(threadId) + "/messages";
    }

    public static String staffMessages(Integer threadId) {
        return base(threadId) + "/staff-messages";
    }

    public static String typing(Integer threadId) {
        return base(threadId) + "/typing";
    }

    public static String read(Integer threadId) {
        return base(threadId) + "/read";
    }

    public static String status(Integer threadId) {
        return base(threadId) + "/status";
    }
}
