// Contributor(s): Robbie
// Main: Robbie - Auth and account JSON DTOs for REST and OpenAPI.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "RegisterRequest", description = "New customer account payload with optional employee link fields.")
@Getter
@Setter
public class RegisterRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Unique handle for login.")
    @NotBlank
    @Size(max = 50)
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Unique mailbox used for login and receipts.")
    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @Schema(description = "Optional phone used to match prior guest checkout rows during registration.")
    @Size(max = 30)
    private String phone;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Initial password for the new account.")
    @NotBlank
    private String password;

    @Schema(description = "Required when email matches an unlinked employee work inbox. Must match that employee user password so linking is safe.")
    @Size(max = 128)
    private String employeeLinkPassword;
}
