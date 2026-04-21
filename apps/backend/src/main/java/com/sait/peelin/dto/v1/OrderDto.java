// Contributor(s): Samantha
// Main: Samantha - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.OrderMethod;
import com.sait.peelin.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(name = "OrderDto", description = "Order header money breakdown and nested line items for detail views.")
public record OrderDto(
        @Schema(description = "Primary key UUID.") UUID id,
        @Schema(description = "Customer facing order number.") String orderNumber,
        @Schema(description = "Owning customer profile id when present.") UUID customerId,
        @Schema(description = "Display name for receipts.") String customerName,
        @Schema(description = "Fulfilling bakery id.") Integer bakeryId,
        @Schema(description = "Bakery display label.") String bakeryName,
        @Schema(description = "Saved address id when used.") Integer addressId,
        @Schema(description = "Pickup delivery or other channel.") OrderMethod orderMethod,
        @Schema(description = "Workflow status enum.") OrderStatus status,
        @Schema(description = "Merchandise total before discounts tax and fees.") BigDecimal orderTotal,
        @Schema(description = "Promotional or manual discount bucket.") BigDecimal orderDiscount,
        @Schema(description = "Tax rate applied as a decimal fraction.") BigDecimal orderTaxRate,
        @Schema(description = "Computed tax money.") BigDecimal orderTaxAmount,
        @Schema(description = "Final charged total.") BigDecimal orderGrandTotal,
        @Schema(description = "Server clock instant when the row was created.") OffsetDateTime placedAt,
        @Schema(description = "Requested service instant when scheduled flows apply.") OffsetDateTime scheduledAt,
        @Schema(description = "Driver completion instant when recorded.") OffsetDateTime deliveredAt,
        @Schema(description = "Customer or staff free text notes.") String comment,
        @Schema(description = "True when a location review already exists or awaits moderation for this order.") boolean locationReviewSubmitted,
        @Schema(description = "Daily special discount portion.") BigDecimal orderSpecialDiscountAmount,
        @Schema(description = "Loyalty tier discount portion.") BigDecimal orderTierDiscountAmount,
        @Schema(description = "Linked employee household discount portion.") BigDecimal orderEmployeeDiscountAmount,
        @Schema(description = "Delivery fee line.") BigDecimal deliveryFee,
        @Schema(description = "Expanded line items with batch and price detail.") List<OrderItemDto> items
) {}
