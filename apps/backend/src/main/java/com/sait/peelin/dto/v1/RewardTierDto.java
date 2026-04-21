// Contributor(s): Owen
// Main: Owen - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "RewardTierDto", description = "Loyalty tier band with optional upper bound and discount percent.")
public record RewardTierDto(
        @Schema(description = "Tier id.") Integer id,
        @Schema(description = "Marketing name for the tier.") String name,
        @Schema(description = "Minimum lifetime points inclusive.") int minPoints,
        @Schema(description = "Maximum points inclusive before the next tier or null for top tier.") Integer maxPoints,
        @Schema(description = "Catalog discount percent for this tier.") BigDecimal discountRatePercent
) {}
