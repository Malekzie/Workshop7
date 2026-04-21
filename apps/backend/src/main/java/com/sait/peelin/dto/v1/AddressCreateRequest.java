// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "AddressCreateRequest", description = "Admin payload to insert a reusable postal row.")
public record AddressCreateRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Street line one up to 120 characters.")
        @NotBlank @Size(max = 120) String line1,
        @Schema(description = "Optional second line up to 120 characters.")
        @Size(max = 120) String line2,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "City up to 120 characters.")
        @NotBlank @Size(max = 120) String city,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Province or state up to 80 characters.")
        @NotBlank @Size(max = 80) String province,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Postal or ZIP up to 10 characters.")
        @NotBlank @Size(max = 10) String postalCode
) {}
