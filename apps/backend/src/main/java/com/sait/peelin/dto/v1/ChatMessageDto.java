// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "ChatMessageDto", description = "Single support chat line with sender and delivery flags.")
public record ChatMessageDto(
        @Schema(description = "Message row id.") Integer id,
        @Schema(description = "Parent thread id.") Integer threadId,
        @Schema(description = "Author user id.") UUID senderUserId,
        @Schema(description = "Plain text body.") String text,
        @Schema(description = "Server instant when stored.") OffsetDateTime sentAt,
        @Schema(description = "True after the recipient read cursor passes this row.") boolean read,
        @Schema(description = "True for automated system lines.") boolean isSystem,
        @Schema(description = "True when only staff should see the row in UI.") boolean staffOnly
) {}
