// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "StartConversationRequest", description = "Identifies the other staff user when opening a DM.")
public record StartConversationRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Peer user id to message.")
        @NotNull UUID recipientId
) {}
