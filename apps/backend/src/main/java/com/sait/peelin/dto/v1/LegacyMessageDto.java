// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "LegacyMessageDto", description = "Legacy user to user inbox row before thread chat.")
public record LegacyMessageDto(
        @Schema(description = "Message id.") UUID id,
        @Schema(description = "Sender user id.") UUID senderId,
        @Schema(description = "Recipient user id.") UUID receiverId,
        @Schema(description = "Short subject line.") String subject,
        @Schema(description = "Message body.") String content,
        @Schema(description = "Send instant.") OffsetDateTime sentAt,
        @Schema(description = "Read flag for the viewer.") boolean read
) {}
