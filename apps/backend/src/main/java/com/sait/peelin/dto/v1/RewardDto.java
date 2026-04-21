// Contributor(s): Owen
// Main: Owen - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "RewardDto", description = "Loyalty points ledger entry for a customer.")
public record RewardDto(
        @Schema(description = "Reward row id.") UUID id,
        @Schema(description = "Customer who earned points.") UUID customerId,
        @Schema(description = "Related order id when points came from a purchase.") UUID orderId,
        @Schema(description = "Points delta for the event.") int pointsEarned,
        @Schema(description = "Posting instant.") OffsetDateTime transactionDate
) {}
