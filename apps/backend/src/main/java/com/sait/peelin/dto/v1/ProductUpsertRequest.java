// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "ProductUpsertRequest", description = "Admin create or replace payload for catalog products.")
@Data
public class ProductUpsertRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Short shelf label up to 120 characters.")
    @NotBlank
    @Size(max = 120)
    private String name;
    @Schema(description = "Optional long description up to 1000 characters.")
    @Size(max = 1000)
    private String description;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Base price zero or positive.")
    @NotNull
    @PositiveOrZero
    private BigDecimal basePrice;
    @Schema(description = "Optional media URL up to 500 characters.")
    @Size(max = 500)
    private String imageUrl;
    @Schema(description = "Tag ids to attach after save.")
    private List<Integer> tagIds;
}
