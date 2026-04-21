// Contributor(s): Owen
// Main: Owen - Order checkout payment or loyalty JSON for API responses and requests.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(name = "RewardTierUpsertRequest", description = "Admin payload to create or replace a loyalty tier band.")
@Data
public class RewardTierUpsertRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Tier display name.")
    @NotBlank
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Lower points threshold inclusive.")
    @NotNull
    private Integer minPoints;
    @Schema(description = "Upper points threshold inclusive or null for open ended top tier.")
    private Integer maxPoints;
    @Schema(description = "Discount percent applied at checkout for members in this band.")
    private BigDecimal discountRatePercent;
}
