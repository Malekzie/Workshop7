// Contributor(s): Robbie
// Main: Robbie - Employee profile admin and self-service JSON DTO.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(name = "EmployeeUpsertRequest", description = "Admin create or replace payload for employee HR records.")
public record EmployeeUpsertRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Existing user id to attach.")
        @NotNull UUID userId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Home bakery id.")
        @NotNull Integer bakeryId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Postal address id.")
        @NotNull Integer addressId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Given name up to 50 characters.")
        @NotBlank @Size(max = 50) String firstName,
        @Schema(description = "Middle initial or blank.")
        @Size(max = 1) @Pattern(regexp = "^$|^[A-Za-z]$", message = "Middle initial must be a single letter") String middleInitial,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Family name up to 50 characters.")
        @NotBlank @Size(max = 50) String lastName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Position title up to 40 characters.")
        @NotBlank @Size(max = 40) String position,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Primary phone up to 20 characters.")
        @NotBlank @Size(max = 20) String phone,
        @Schema(description = "Business phone up to 20 characters.")
        @Size(max = 20) String businessPhone,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Work email up to 254 characters.")
        @NotBlank @Size(max = 254) String workEmail
) {}
