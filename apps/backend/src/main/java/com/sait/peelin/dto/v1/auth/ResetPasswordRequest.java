// Contributor(s): Robbie
// Main: Robbie - Auth and account JSON DTOs for REST and OpenAPI.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ResetPasswordRequest", description = "Emailed reset token plus new password after the user opens the link.")
public class ResetPasswordRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Opaque token from the reset email.")
    @NotBlank
    private String token;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Replacement password within allowed length bounds.")
    @NotBlank
    @Size(min = 7, max = 72)
    private String newPassword;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
