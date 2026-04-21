// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.BakeryStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "BakeryDto", description = "Bakery location with contact geo and nested address.")
public record BakeryDto(
        @Schema(description = "Bakery id.") Integer id,
        @Schema(description = "Public display name.") String name,
        @Schema(description = "Voice line for the shop.") String phone,
        @Schema(description = "Contact mailbox.") String email,
        @Schema(description = "Open closed or seasonal status enum.") BakeryStatus status,
        @Schema(description = "Map latitude decimal.") BigDecimal latitude,
        @Schema(description = "Map longitude decimal.") BigDecimal longitude,
        @Schema(description = "Hero image URL for cards.") String bakeryImageUrl,
        @Schema(description = "Postal address for directions.") AddressDto address
) {}
