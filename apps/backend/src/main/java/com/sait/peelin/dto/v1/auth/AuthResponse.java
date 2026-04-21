// Contributor(s): Robbie
// Main: Robbie - Auth and account JSON DTOs for REST and OpenAPI.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Schema(name = "AuthResponse", description = "JWT plus embedded user summary returned after login or profile refresh.")
public class AuthResponse {

    @Schema(description = "Bearer token value for Authorization header on API calls.")
    @Getter
    @Setter
    private String token;

    @Schema(description = "Display username tied to the session.")
    @Getter
    @Setter
    private String username;

    @Schema(description = "Primary role string such as CUSTOMER or ADMIN.")
    @Getter
    @Setter
    private String role;

    @Schema(description = "Stable user id for the authenticated row.")
    @Getter
    @Setter
    private UUID userId;

    @Schema(description = "Primary email on the account.")
    @Getter
    @Setter
    private String email;

    @Schema(description = "Public profile image URL when the account has an approved or pending photo.")
    @Getter
    @Setter
    private String profilePhotoPath;

    @Schema(description = "Employee given name when an employee profile exists.")
    @Getter
    @Setter
    private String firstName;

    @Schema(description = "Employee family name when an employee profile exists.")
    @Getter
    @Setter
    private String lastName;

    @Schema(description = "True when a guest customer row already exists for this email or optional phone.")
    @Getter
    @Setter
    private Boolean priorGuestCheckout;

    @Schema(description = "Optional UI hint about finishing a linked guest profile.")
    @Getter
    @Setter
    private String guestProfileCompletionMessage;

    @Schema(description = "True when registration just created an employee to customer discount link with both sides active.")
    @Getter
    @Setter
    private Boolean employeeDiscountLinkEstablished;

    @Schema(description = "Optional UI copy describing the new employee discount link outcome.")
    @Getter
    @Setter
    private String employeeDiscountLinkMessage;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String role, UUID userId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.userId = userId;
    }
}
