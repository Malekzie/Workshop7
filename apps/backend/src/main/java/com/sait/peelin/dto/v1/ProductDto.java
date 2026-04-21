// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "ProductDto", description = "Catalog product row with tags for storefront and admin lists.")
public record ProductDto(
        @Schema(description = "Product primary key.") Integer id,
        @Schema(description = "Display title.") String name,
        @Schema(description = "Longer marketing or ingredient copy.") String description,
        @Schema(description = "Base list price before specials.") BigDecimal basePrice,
        @Schema(description = "Primary image URL for cards.") String imageUrl,
        @Schema(description = "Tag ids attached to this product.") List<Integer> tagIds
) {}
