// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AddressSummaryDto", description = "Compact address row for admin pick lists.")
public record AddressSummaryDto(
        @Schema(description = "Address id.") Integer id,
        @Schema(description = "Street line one.") String line1,
        @Schema(description = "Street line two.") String line2,
        @Schema(description = "City or town.") String city,
        @Schema(description = "Province or state.") String province,
        @Schema(description = "Postal or ZIP code.") String postalCode
) {}
