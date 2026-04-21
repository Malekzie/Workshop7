// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "TagCreateRequest", description = "Admin payload to create a catalog tag.")
@Data
public class TagCreateRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Tag label up to 50 characters.")
    @NotBlank
    @Size(max = 50)
    private String name;

    @Schema(description = "True when the tag should appear in dietary filters.")
    private boolean dietary;
}
