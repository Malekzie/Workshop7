// Contributor(s): Samantha
// Main: Samantha - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "CheckoutSessionResponse", description = "New order summary plus Stripe Payment Sheet fields when card tender is used.")
public record CheckoutSessionResponse(
        @Schema(description = "Internal order id for follow-up calls.") UUID orderId,
        @Schema(description = "Human readable order number for receipts.") String orderNumber,
        @Schema(description = "Stripe client secret for Payment Element or Sheet.") String clientSecret,
        @Schema(description = "Stripe PaymentIntent id for confirm calls.") String paymentIntentId,
        @Schema(description = "Merchandise subtotal before tax and fees.") BigDecimal subtotal,
        @Schema(description = "Combined discount total before tax.") BigDecimal discount,
        @Schema(description = "Delivery fee component when applicable.") BigDecimal deliveryFee,
        @Schema(description = "Sales tax amount for the jurisdiction.") BigDecimal taxAmount,
        @Schema(description = "Final total charged or authorized.") BigDecimal grandTotal
) {}
