// Contributor(s): Robbie
// Main: Robbie - Admin or dashboard JSON DTO for staff tools.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "UserSummaryDto", description = "Compact user row for admin lists and messaging pickers.")
public record UserSummaryDto(
        @Schema(description = "User primary key.") UUID id,
        @Schema(description = "Login handle.") String username,
        @Schema(description = "Mailbox on file.") String email,
        @Schema(description = "Assigned role enum.") UserRole role,
        @Schema(description = "False when the account is disabled for login.") boolean active
) {}
