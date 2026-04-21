// Contributor(s): Robbie
// Main: Robbie - Auth and account JSON DTOs for REST and OpenAPI.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Partial update for the authenticated user username or sign-in email or both.
 * Null fields are left unchanged.
 */
@Schema(name = "AccountProfilePatchRequest", description = "Sparse update for account identity fields on the signed-in user.")
@Getter
@Setter
public class AccountProfilePatchRequest {

    @Schema(description = "New username when changing handle. Omit to keep current value.")
    private String username;

    @Schema(description = "New login email when migrating mailbox. Omit to keep current value.")
    private String email;
}
