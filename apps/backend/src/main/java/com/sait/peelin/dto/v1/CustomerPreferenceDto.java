// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.PreferenceType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CustomerPreferenceDto", description = "Single taste tag preference row for AI and storefront filters.")
public record CustomerPreferenceDto(
        @Schema(description = "Catalog tag id.") Integer tagId,
        @Schema(description = "Tag display name.") String tagName,
        @Schema(description = "Like dislike or neutral enum.") PreferenceType preferenceType,
        @Schema(description = "Strength score for ranking.") Short preferenceStrength
) {}
