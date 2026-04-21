// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "TypingPayload", description = "WebSocket typing indicator for support or staff channels.")
public record TypingPayload(
        @Schema(description = "User emitting the indicator.") UUID userId,
        @Schema(description = "True while the composer has focus activity.") boolean typing
) {}
