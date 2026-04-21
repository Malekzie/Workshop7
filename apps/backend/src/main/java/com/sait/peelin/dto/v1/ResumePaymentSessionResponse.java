// Contributor(s): Samantha
// Main: Samantha - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Result of resuming checkout for an order still in {@code pending_payment}.
 * When {@code orderPaid} is true the Stripe payment already finished and the order row was synced so no client sheet is needed.
 */
@Schema(name = "ResumePaymentSessionResponse", description = "Either fresh Payment Sheet secrets or a flag that the order already paid.")
public record ResumePaymentSessionResponse(
        @Schema(description = "Order id being resumed.") UUID orderId,
        @Schema(description = "Public order number for UI display.") String orderNumber,
        @Schema(description = "Client secret when a new sheet must open. Empty when orderPaid is true.") String clientSecret,
        @Schema(description = "PaymentIntent id tied to the pending charge.") String paymentIntentId,
        @Schema(description = "True when Stripe already captured funds and no sheet is required.") boolean orderPaid
) {}
