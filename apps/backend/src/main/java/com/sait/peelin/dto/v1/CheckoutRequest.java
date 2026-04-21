// Contributor(s): Samantha
// Main: Samantha - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.OrderMethod;
import com.sait.peelin.model.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(name = "CheckoutRequest", description = "Cart lines plus fulfillment context for guest or signed-in checkout.")
@Data
public class CheckoutRequest {

    @Schema(description = "When set staff places the order for that customer instead of self-service checkout.")
    private UUID customerId;

    @Schema(description = "Optional extra discount amount applied before tier pricing when staff overrides apply.")
    private BigDecimal manualDiscount;

    @Schema(description = "Client local calendar date for daily special pricing in yyyy-MM-dd form. Defaults to America Edmonton bakery day when omitted.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pricingLocalDate;

    @Schema(description = "Guest contact block required when no JWT is present.")
    @Valid
    private GuestCustomerRequest guest;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Bakery id fulfilling the order.")
    @NotNull
    private Integer bakeryId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Pickup delivery or other supported channel.")
    @NotNull
    private OrderMethod orderMethod;

    @Schema(description = "Saved customer address id when using a stored profile address.")
    private Integer addressId;

    @Schema(description = "Free-form kitchen or driver notes.")
    private String comment;

    @Schema(description = "Scheduled pickup or delivery instant when applicable.")
    private OffsetDateTime scheduledAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Card cash or other tender selection.")
    @NotNull
    private PaymentMethod paymentMethod;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Line items with product ids and quantities.")
    @NotEmpty
    @Valid
    private List<CheckoutLineRequest> items;

    @Schema(description = "Inline street snapshot that overrides addressId for this order only.")
    @Valid
    private InlineAddressRequest deliveryAddress;

    @Schema(name = "InlineAddressRequest", description = "One-time delivery address fields when not using a saved profile row.")
    @Data
    public static class InlineAddressRequest {
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Size(max = 120)
        private String line1;
        @Size(max = 120)
        private String line2;
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Size(max = 120)
        private String city;
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Size(max = 80)
        private String province;
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Postal code up to seven characters.")
        @NotNull
        @Size(max = 7)
        private String postalCode;
    }

    @Schema(name = "CheckoutLineRequest", description = "Single cart row with optional batch selection.")
    @Data
    public static class CheckoutLineRequest {
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private Integer productId;
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        private Integer quantity;
        @Schema(description = "Inventory batch id when the client picked a specific bake batch.")
        private Integer batchId;
    }
}
