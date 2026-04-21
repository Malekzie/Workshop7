// Contributor(s): Mason
// Main: Mason - Product recommendations for signed-in customers.

package com.sait.peelin.controller.v1;

import com.sait.peelin.dto.v1.ProductRecommendationDto;
import com.sait.peelin.model.Customer;
import com.sait.peelin.repository.CustomerRepository;
import com.sait.peelin.service.CurrentUserService;
import com.sait.peelin.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Personalized product recommendations at {@code /api/v1/recommendations}.
 */
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Personalized product picks for signed-in customers with a profile row.")
@SecurityRequirement(name = "bearer-jwt")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final CurrentUserService currentUserService;
    private final CustomerRepository customerRepository;

    @Operation(summary = "List my recommendations", description = "Returns ranked picks for the signed-in customer. Responds 400 when the account has no customer profile. Returns an empty list when the engine cannot produce results.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations or empty list returned"),
            @ApiResponse(responseCode = "400", description = "Customer profile required", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    @GetMapping
    public List<ProductRecommendationDto> getRecommendations() {
        try {
            var user = currentUserService.requireUser();
            Customer customer = customerRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer profile required"));
            return recommendationService.listRecommendationsForCustomer(customer.getId());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return List.of();
        }
    }
}
