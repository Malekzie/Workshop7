// Contributor(s): Owen
// Main: Owen - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Schema(name = "ReviewCreateRequest", description = "Customer or guest payload to create a product or bakery review.")
@Data
public class ReviewCreateRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Star rating from one through five.")
    @NotNull
    @Min(1)
    @Max(5)
    private Short rating;
    @Schema(description = "Optional prose up to 2000 characters.")
    @Size(max = 2000)
    private String comment;
    @Schema(description = "Order id when the review is tied to a delivered purchase.")
    private UUID orderId;
    @Schema(description = "Guest display name when the caller is not authenticated.")
    @Size(max = 100)
    private String guestName;
}
