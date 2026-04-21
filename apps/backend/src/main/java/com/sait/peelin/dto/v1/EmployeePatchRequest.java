// Contributor(s): Robbie
// Main: Robbie - Employee profile admin and self-service JSON DTO.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "EmployeePatchRequest", description = "Sparse update for employee contact and address fields.")
public record EmployeePatchRequest(
        @Schema(description = "New given name when provided.") String firstName,
        @Schema(description = "Single letter middle initial or blank.")
        @Size(max = 1) @Pattern(regexp = "^$|^[A-Za-z]$", message = "Middle initial must be a single letter") String middleInitial,
        @Schema(description = "New family name when provided.") String lastName,
        @Schema(description = "Primary phone when provided.") String phone,
        @Schema(description = "Business phone when provided.") String businessPhone,
        @Schema(description = "Work email when provided.") String workEmail,
        @Schema(description = "Address id pointer when provided.") Integer addressId,
        @Schema(description = "Inline address upsert when replacing street data.") AddressUpsertRequest address
) {}
