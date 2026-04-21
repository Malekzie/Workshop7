// Contributor(s): Samantha
// Main: Samantha - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "OrderItemDto", description = "Single priced line on an order with catalog references.")
public record OrderItemDto(
        @Schema(description = "Line row id when persisted.") Integer id,
        @Schema(description = "Catalog product id.") Integer productId,
        @Schema(description = "Product title at time of sale.") String productName,
        @Schema(description = "Image URL snapshot for receipts.") String productImageUrl,
        @Schema(description = "Bake batch id when the client picked inventory.") Integer batchId,
        @Schema(description = "Quantity purchased.") int quantity,
        @Schema(description = "Unit price before line total math.") BigDecimal unitPrice,
        @Schema(description = "Extended line money.") BigDecimal lineTotal,
        @Schema(description = "True when any product review row exists for this customer and product pair.") boolean productReviewSubmitted
) {}
