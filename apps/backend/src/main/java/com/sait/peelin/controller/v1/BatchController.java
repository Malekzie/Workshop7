// Contributor(s): Mason
// Main: Mason - Production batches per bakery for pickup and inventory views.

package com.sait.peelin.controller.v1;

import com.sait.peelin.dto.v1.BatchDto;
import com.sait.peelin.service.BatchQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Bakery batch availability queries on paths under {@code /api/v1}.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Batches", description = "Inventory batch queries. Public callers may pass activeOnly true to return only batches that still have stock.")
public class BatchController {

    private final BatchQueryService batchQueryService;

    @Operation(summary = "List batches for a bakery", description = "Returns inventory batches for a bakery. Public callers can read this list for location catalogs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batches returned"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bakery not found", content = @Content)
    })
    @GetMapping("/bakeries/{bakeryId}/batches")
    public List<BatchDto> byBakery(
            @PathVariable Integer bakeryId,
            @Parameter(description = "When true returns only batches that still have remaining stock", example = "false")
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        return batchQueryService.byBakery(bakeryId, activeOnly);
    }

    @Operation(summary = "List batches for a product", description = "Returns all inventory batches for a given product across all bakeries.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batches returned"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/products/{productId}/batches")
    public List<BatchDto> byProduct(@PathVariable Integer productId) {
        return batchQueryService.byProduct(productId);
    }
}
