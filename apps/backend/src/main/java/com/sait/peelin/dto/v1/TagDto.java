// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TagDto", description = "Product category or dietary tag for filters and cards.")
public record TagDto(
        @Schema(description = "Tag id.") Integer id,
        @Schema(description = "Display name.") String name,
        @Schema(description = "True when the tag denotes a dietary restriction.") boolean dietary
) {}
