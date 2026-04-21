// Contributor(s): Robbie
// Main: Robbie - Chat or staff messaging JSON for REST and WebSocket clients.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "PostChatMessageRequest", description = "Customer or staff body when appending to a support thread.")
@Data
public class PostChatMessageRequest {
    @Schema(description = "Optional message text up to 2000 characters.")
    @Size(max = 2000)
    private String text;
}
