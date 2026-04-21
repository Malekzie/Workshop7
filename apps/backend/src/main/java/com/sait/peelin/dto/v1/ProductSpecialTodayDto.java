// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "ProductSpecialTodayDto", description = "Slim today special payload. Product id is null when no row exists for the requested date.")
public record ProductSpecialTodayDto(
        @Schema(description = "Featured product id or null when absent.") Integer productId,
        @Schema(description = "Percent discount for the day.") BigDecimal discountPercent
) {}
