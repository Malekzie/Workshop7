// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Schema(name = "SendLegacyMessageRequest", description = "Payload for legacy direct messages between user accounts.")
@Data
public class SendLegacyMessageRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Target user id.")
    @NotNull
    private UUID receiverId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Subject up to 255 characters.")
    @NotBlank
    @Size(max = 255)
    private String subject;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Body up to 2000 characters.")
    @NotBlank
    @Size(max = 2000)
    private String content;
}
