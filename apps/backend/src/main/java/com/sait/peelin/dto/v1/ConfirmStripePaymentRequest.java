// Contributor(s): Samantha
// Main: Samantha - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ConfirmStripePaymentRequest", description = "Stripe PaymentIntent id the client received after Sheet success.")
public record ConfirmStripePaymentRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "PaymentIntent id from Stripe.js or mobile SDK.")
        @NotBlank String paymentIntentId
) {}
