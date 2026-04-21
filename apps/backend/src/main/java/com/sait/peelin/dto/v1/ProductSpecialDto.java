// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "ProductSpecialDto", description = "Featured product discount row for a calendar day with catalog snapshot.")
public record ProductSpecialDto(
        @Schema(description = "Special row id.") Integer productSpecialId,
        @Schema(description = "Calendar day for the feature.") LocalDate featuredOn,
        @Schema(description = "Percent discount applied to base price.") BigDecimal discountPercent,
        @Schema(description = "Linked product id.") Integer productId,
        @Schema(description = "Product title snapshot.") String productName,
        @Schema(description = "Product description snapshot.") String productDescription,
        @Schema(description = "Base price snapshot.") BigDecimal productBasePrice,
        @Schema(description = "Image URL snapshot.") String productImageUrl
) {}
