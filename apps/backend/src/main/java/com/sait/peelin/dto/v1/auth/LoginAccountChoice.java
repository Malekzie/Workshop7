// Contributor(s): Robbie
// Main: Robbie - Auth and account JSON DTOs for REST and OpenAPI.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginAccountChoice", description = "Single selectable account during multi-role login resolution.")
public record LoginAccountChoice(
        @Schema(description = "Handle shown after the user picks this row.") String username,
        @Schema(description = "Role string such as CUSTOMER or EMPLOYEE.") String role,
        @Schema(description = "Longer label for list rendering.") String label
) {}
