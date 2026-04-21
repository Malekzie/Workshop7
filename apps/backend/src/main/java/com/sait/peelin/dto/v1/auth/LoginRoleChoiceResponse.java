// Contributor(s): Robbie
// Main: Robbie - Auth and account JSON DTOs for REST and OpenAPI.

package com.sait.peelin.dto.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "LoginRoleChoiceResponse", description = "Returned when one mailbox maps to multiple roles so the client must pick one.")
public record LoginRoleChoiceResponse(
        @Schema(description = "Human readable prompt for the chooser UI.") String message,
        @Schema(description = "Each row is one account the user may continue as.") List<LoginAccountChoice> choices
) {}
