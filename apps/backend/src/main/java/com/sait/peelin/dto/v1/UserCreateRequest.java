// Contributor(s): Robbie
// Main: Robbie - Admin or dashboard JSON DTO for staff tools.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "UserCreateRequest", description = "Admin-only payload to provision employee or customer login rows.")
@Getter
@Setter
public class UserCreateRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Unique handle up to 50 characters.")
    @NotBlank
    @Size(max = 50)
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Unique mailbox up to 254 characters.")
    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Initial password between six and 128 characters.")
    @NotBlank
    @Size(min = 6, max = 128)
    private String password;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Literal employee or customer string for role assignment.")
    @NotBlank
    @Pattern(regexp = "employee|customer", message = "Role must be 'employee' or 'customer'")
    private String role;
}
