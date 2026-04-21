// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "ProductSpecialUpsertRequest", description = "Admin create or replace body for daily featured specials. Server owns the primary key.")
@Data
public class ProductSpecialUpsertRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Catalog product id to feature.")
    @NotNull
    private Integer productId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Calendar day in ISO date form.")
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate featuredOn;

    @Schema(description = "Percent discount between zero and fifty. Defaults to zero when omitted.")
    @DecimalMin("0.00")
    @DecimalMax("50.00")
    private BigDecimal discountPercent = BigDecimal.ZERO;
}
