// Contributor(s): Robbie
// Main: Robbie - Auth and account JSON DTOs for REST and OpenAPI.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Schema(name = "LoginRequest", description = "Sign-in payload. Send username or email with password.")
@Data
public class LoginRequest {

    @Schema(description = "Optional account name. Leave blank to use email as the principal.")
    private String username;

    @Schema(description = "Optional email address. Leave blank when username is set.")
    private String email;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Plain text password")
    @NotBlank
    private String password;

    @Schema(description = "When true extends remembered session lifetime on supported clients.")
    private boolean rememberMe;

    @AssertTrue(message = "username or email must be provided")
    public boolean isLoginPrincipalProvided() {
        return (username != null && !username.trim().isEmpty())
                || (email != null && !email.trim().isEmpty());
    }
}
