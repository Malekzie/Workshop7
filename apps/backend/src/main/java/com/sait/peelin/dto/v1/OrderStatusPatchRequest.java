// Contributor(s): Samantha
// Main: Samantha - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(name = "OrderStatusPatchRequest", description = "Staff workflow transition for order status.")
@Data
public class OrderStatusPatchRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Target status enum after validation rules pass.")
    @NotNull
    private OrderStatus status;
}
