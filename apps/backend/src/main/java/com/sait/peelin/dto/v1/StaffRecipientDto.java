// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "StaffRecipientDto", description = "Selectable admin or employee when starting a staff DM.")
public record StaffRecipientDto(
        @Schema(description = "Target user id.") UUID userId,
        @Schema(description = "Login handle.") String username,
        @Schema(description = "Role label for list rendering.") String role
) {}
