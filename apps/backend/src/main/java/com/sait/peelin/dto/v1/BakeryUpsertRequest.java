// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.BakeryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Schema(name = "BakeryUpsertRequest", description = "Admin create or replace payload for bakery locations.")
@Data
public class BakeryUpsertRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Display name up to 100 characters.")
    @NotBlank
    @Size(max = 100)
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Voice phone up to 20 characters.")
    @NotBlank
    @Size(max = 20)
    private String phone;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Contact email up to 254 characters.")
    @NotBlank
    @Email
    @Size(max = 254)
    private String email;
    @Schema(description = "Operational status enum.")
    private BakeryStatus status;
    @Schema(description = "Map latitude when geocoded.")
    private BigDecimal latitude;
    @Schema(description = "Map longitude when geocoded.")
    private BigDecimal longitude;
    @Schema(description = "Optional hero image URL up to 2048 characters.")
    @Size(max = 2048)
    private String bakeryImageUrl;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Nested postal address payload.")
    @NotNull
    @Valid
    private AddressUpsertRequest address;
}
