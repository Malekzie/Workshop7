// Contributor(s): Robbie
// Main: Robbie - Auth and account JSON DTOs for REST and OpenAPI.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "ChangePasswordRequest", description = "Current password check plus new password for account password rotation.")
@Getter
@Setter
public class ChangePasswordRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Existing password for verification.")
    @NotBlank
    private String currentPassword;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Replacement password within allowed length bounds.")
    @NotBlank
    @Size(min = 7, max = 72)
    private String newPassword;
}
