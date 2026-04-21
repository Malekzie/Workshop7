// Contributor(s): Samantha
// Main: Samantha - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Schema(name = "OrderDeliveredPatchRequest", description = "Optional delivery instant override when staff records drop-off.")
@Data
public class OrderDeliveredPatchRequest {
    @Schema(description = "Clock instant to store as deliveredAt. Omit to default to server now.")
    private OffsetDateTime deliveredAt;
}
