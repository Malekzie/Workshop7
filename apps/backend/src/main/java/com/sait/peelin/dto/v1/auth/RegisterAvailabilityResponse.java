// Contributor(s): Robbie
// Main: Robbie - Registration field availability flags for username email and phone.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pre-check before multi-step registration where the username must be unique and the sign-in email must not belong to another
 * customer or admin account. Employee accounts may share that email so customer registration can match work email
 * for employee-customer linking.
 */
@Schema(name = "RegisterAvailabilityResponse", description = "Flags returned before register to drive client validation steps.")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAvailabilityResponse {

    @Schema(description = "False when the username is already taken.")
    private boolean usernameAvailable;

    @Schema(description = "False when the email is blocked for a new customer row.")
    private boolean emailAvailable;

    @Schema(description = "True when the email matches exactly one unlinked employee work inbox so the client should collect that employee password before register.")
    private boolean employeeLinkOffered;

    @Schema(description = "True when the email matches guest checkout rows that can merge after registration.")
    private boolean guestEmailLinkOffered;

    @Schema(description = "True when the phone matches guest checkout rows that can merge after registration.")
    private boolean guestPhoneLinkOffered;
}
