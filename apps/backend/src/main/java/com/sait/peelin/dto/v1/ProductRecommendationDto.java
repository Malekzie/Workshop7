// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductRecommendationDto", description = "Minimal product pick returned by the recommendation engine.")
public record ProductRecommendationDto(
        @Schema(description = "Catalog product id.") Integer productId,
        @Schema(description = "Display title for cards.") String productName
) {}
