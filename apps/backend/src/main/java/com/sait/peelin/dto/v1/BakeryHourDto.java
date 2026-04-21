// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

@Schema(name = "BakeryHourDto", description = "Weekly hour band for one weekday at a bakery.")
public record BakeryHourDto(
        @Schema(description = "Hour row id.") Integer id,
        @Schema(description = "Weekday index matching server convention.") short dayOfWeek,
        @Schema(description = "Opening local time when not closed.") LocalTime openTime,
        @Schema(description = "Closing local time when not closed.") LocalTime closeTime,
        @Schema(description = "True when the bakery is closed this weekday.") boolean closed
) {}
