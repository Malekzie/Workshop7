// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "ReadReceiptPayload", description = "WebSocket payload when a user advances read state on chat.")
public record ReadReceiptPayload(
        @Schema(description = "User who read up to a point.") UUID userId,
        @Schema(description = "Server instant associated with the read event.") OffsetDateTime readAt
) {}
