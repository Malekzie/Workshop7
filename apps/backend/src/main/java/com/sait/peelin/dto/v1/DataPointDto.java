// Contributor(s): Robbie
// Main: Robbie - Admin or dashboard JSON DTO for staff tools.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "DataPointDto", description = "Chart friendly label plus numeric metric for analytics endpoints.")
public record DataPointDto(
        @Schema(description = "Axis label text for chart ticks.") String label,
        @Schema(description = "Measured value for chart rendering.") BigDecimal value
) {}
