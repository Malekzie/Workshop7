// Contributor(s): Owen
// Main: Owen - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "ReviewDto", description = "Published or moderated review row for bakery or product surfaces.")
public record ReviewDto(
        @Schema(description = "Review primary key.") UUID id,
        @Schema(description = "Author customer id when logged in.") UUID customerId,
        @Schema(description = "Linked order id when the review ties to a purchase.") UUID orderId,
        @Schema(description = "Bakery id for location reviews.") Integer bakeryId,
        @Schema(description = "Bakery display label.") String bakeryName,
        @Schema(description = "Product id for product reviews.") Integer productId,
        @Schema(description = "Staff id when an employee authored the note.") UUID employeeId,
        @Schema(description = "Star rating one through five.") short rating,
        @Schema(description = "Customer prose body.") String comment,
        @Schema(description = "Moderation workflow state.") ReviewStatus status,
        @Schema(description = "Instant the customer submitted the review.") OffsetDateTime submittedAt,
        @Schema(description = "Instant the review became visible after staff or auto approval.") OffsetDateTime approvalDate,
        @Schema(description = "Friendly public label such as first name plus last initial.") String reviewerDisplayName,
        @Schema(description = "Short rejection text when status is rejected from AI moderation or upstream errors.") String moderationMessage,
        @Schema(description = "True when tied to a fulfilled order line.") boolean verifiedPurchase,
        @Schema(description = "True when the author had a signed-in customer profile.") boolean verifiedAccount,
        @Schema(description = "Reviewer headshot URL when present.") String reviewerPhotoUrl,
        @Schema(description = "True while profile photo awaits staff approval.") boolean reviewerPhotoApprovalPending
) {}
