// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "StaffMessageDto", description = "Single staff-to-staff message row.")
public record StaffMessageDto(
        @Schema(description = "Message id.") Integer id,
        @Schema(description = "Parent conversation id.") Integer conversationId,
        @Schema(description = "Author user id.") UUID senderUserId,
        @Schema(description = "Plain text body.") String text,
        @Schema(description = "Server instant when stored.") OffsetDateTime sentAt,
        @Schema(description = "Read flag for the recipient view.") boolean read
) {}
