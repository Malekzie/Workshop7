// Contributor(s): Owen
// Main: Owen - Product reviews averages and moderation-facing read paths.

package com.sait.peelin.controller.v1;

import com.sait.peelin.dto.v1.ReviewCreateRequest;
import com.sait.peelin.dto.v1.ReviewDto;
import com.sait.peelin.dto.v1.ReviewStatusPatchRequest;
import com.sait.peelin.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Product and bakery reviews moderation and averages on paths under {@code /api/v1}.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Customer reviews for products and bakery locations. New reviews pass through AI moderation and manual status changes stay admin-only.")
public class ProductReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "List reviews for product", description = "Returns all approved reviews for a given product.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews returned"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/products/{productId}/reviews")
    public List<ReviewDto> list(@PathVariable Integer productId) {
        return reviewService.forProduct(productId);
    }

    @Operation(summary = "Get average rating for product", description = "Returns the average star rating across all approved reviews for a product.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Average rating returned (null if no reviews)"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/products/{productId}/reviews/average")
    public Double average(@PathVariable Integer productId) {
        return reviewService.averageForProduct(productId);
    }

    @Operation(summary = "List reviews for bakery", description = "Returns all approved reviews for a bakery location.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews returned")
    })
    @GetMapping("/bakeries/{bakeryId}/reviews")
    public List<ReviewDto> listForBakery(@PathVariable Integer bakeryId) {
        return reviewService.forBakery(bakeryId);
    }

    @Operation(summary = "Average bakery rating", description = "Mean star rating across approved reviews for the bakery or null when no reviews exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Average rating returned"),
            @ApiResponse(responseCode = "404", description = "Bakery not found", content = @Content)
    })
    @GetMapping("/bakeries/{bakeryId}/reviews/average")
    public Double averageForBakery(@PathVariable Integer bakeryId) {
        return reviewService.averageForBakery(bakeryId);
    }

    @Operation(summary = "Submit a product review", description = "Submit a review for a product. Signed-in customers use their profile. Guests may send optional guestName in the body.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review submitted"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict when a uniqueness rule blocks the insert", content = @Content)
    })
    @PostMapping("/products/{productId}/reviews")
    public ReviewDto create(@PathVariable Integer productId, @Valid @RequestBody ReviewCreateRequest req) {
        return reviewService.create(productId, req);
    }

    @Operation(summary = "Submit order-linked review", description = "Creates a product review tied to a delivered order using the order id from the path.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review submitted"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/orders/{orderId}/reviews")
    public ReviewDto createForOrder(@PathVariable UUID orderId,
                                      @Valid @RequestBody ReviewCreateRequest req) {
        req.setOrderId(orderId);
        return reviewService.createForOrder(req);
    }

    @Operation(summary = "Update review status", description = "Approve or reject a review as an admin override.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review status updated"),
            @ApiResponse(responseCode = "204", description = "No content when the review is not pending"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PatchMapping("/reviews/{reviewId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewDto> patchStatus(@PathVariable UUID reviewId, @Valid @RequestBody ReviewStatusPatchRequest req) {
        return reviewService.patchStatus(reviewId, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "List pending reviews", description = "Moderation queue for reviews awaiting admin action. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "Pending reviews returned")
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/reviews/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReviewDto> pending() {
        return reviewService.pending();
    }

    @Operation(summary = "Top reviews", description = "Highly rated approved reviews capped by the limit query parameter.")
    @ApiResponse(responseCode = "200", description = "Top reviews returned")
    @GetMapping("/reviews/top")
    public List<ReviewDto> top(@RequestParam(defaultValue = "3") int limit) {
        return reviewService.topReviews(limit);
    }

    @Operation(summary = "Submit a bakery or location review", description = "Submit a review for a location. Optional guestName when not authenticated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review submitted"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping("/bakeries/{bakeryId}/reviews")
    public ReviewDto createForBakery(@PathVariable Integer bakeryId,
                                     @Valid @RequestBody ReviewCreateRequest req) {
        return reviewService.createForBakery(bakeryId, req);
    }
}
