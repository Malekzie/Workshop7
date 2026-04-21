// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "SendStaffMessageRequest", description = "Body for posting into a staff conversation.")
public record SendStaffMessageRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Plain text up to 2000 characters.")
        @NotBlank @Size(max = 2000) String text
) {}
