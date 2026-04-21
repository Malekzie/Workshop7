// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "StaffConversationDto", description = "Staff direct message thread summary for the inbox list.")
public record StaffConversationDto(
        @Schema(description = "Conversation id.") Integer id,
        @Schema(description = "Other participant user id.") UUID otherUserId,
        @Schema(description = "Other participant handle.") String otherUsername,
        @Schema(description = "Other participant avatar URL when set.") String otherProfilePhotoPath,
        @Schema(description = "Other participant role label.") String otherRole,
        @Schema(description = "Last message or metadata touch instant.") OffsetDateTime updatedAt,
        @Schema(description = "Unread count for the signed-in viewer.") int unreadCount
) {}
