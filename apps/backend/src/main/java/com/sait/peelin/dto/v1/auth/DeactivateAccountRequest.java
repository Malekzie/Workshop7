// Contributor(s): Robbie
// Main: Robbie - Auth and account JSON DTOs for REST and OpenAPI.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(name = "DeactivateAccountRequest", description = "Current password confirmation before soft disabling the account.")
@Data
public class DeactivateAccountRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Existing password that must match before deactivation.")
    @NotBlank(message = "Current password is required")
    private String currentPassword;
}
