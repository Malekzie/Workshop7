// Contributor(s): Owen
// Main: Owen - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(name = "ReviewStatusPatchRequest", description = "Admin moderation override for approve or reject paths.")
@Data
public class ReviewStatusPatchRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "New moderation state after staff review.")
    @NotNull
    private ReviewStatus status;
}
