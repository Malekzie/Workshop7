// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(name = "BatchDto", description = "Inventory bake batch row for pickup windows.")
public record BatchDto(
        @Schema(description = "Batch id.") Integer id,
        @Schema(description = "Producing bakery id.") Integer bakeryId,
        @Schema(description = "Product id for this bake.") Integer productId,
        @Schema(description = "Production instant.") OffsetDateTime productionDate,
        @Schema(description = "Sell by or discard instant.") OffsetDateTime expiryDate,
        @Schema(description = "Units produced for this batch.") Integer quantityProduced
) {}
