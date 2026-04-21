// Contributor(s): Robbie
// Main: Robbie - STOMP chat typing and read-receipt handlers for customer support threads.

package com.sait.peelin.controller.ws;

import com.sait.peelin.dto.v1.TypingPayload;
import com.sait.peelin.service.ChatService;
import com.sait.peelin.service.ChatTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * STOMP message mappings under {@code /app/chat/...} for typing indicators and read state.
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/thread/{threadId}/typing")
    public void typing(@DestinationVariable Integer threadId, TypingPayload payload) {
        messagingTemplate.convertAndSend(ChatTopics.typing(threadId), payload);
    }

    @MessageMapping("/chat/thread/{threadId}/read")
    public void markRead(@DestinationVariable Integer threadId) {
        chatService.markRead(threadId);
    }
}
