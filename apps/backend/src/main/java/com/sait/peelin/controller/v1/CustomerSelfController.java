// Contributor(s): Mason, Owen
// Main: Mason - Authenticated customer profile preferences and bootstrap payloads.
// Assistance: Owen - Employee-linked customer discount rules via services.

package com.sait.peelin.controller.v1;

import com.sait.peelin.dto.v1.*;
import com.sait.peelin.service.CustomerPreferenceService;
import com.sait.peelin.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Authenticated customer profile preferences and bootstrap under {@code /api/v1/customers/me}.
 */
@PreAuthorize("hasRole('CUSTOMER')")
@RestController
@RequestMapping("/api/v1/customers/me")
@RequiredArgsConstructor
@Tag(name = "Customer profile", description = "Read and update the authenticated customer profile and saved preferences.")
@SecurityRequirement(name = "bearer-jwt")
public class CustomerSelfController {

    private final CustomerService customerService;
    private final CustomerPreferenceService customerPreferenceService;

    @Operation(summary = "Get my profile", description = "Returns the full profile for the currently authenticated customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not a customer account", content = @Content)
    })
    @GetMapping
    public CustomerDto me() {
        return customerService.me();
    }

    @Operation(summary = "Bootstrap my profile", description = "Creates the customer row after first sign-up when the bootstrap payload is valid.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not a customer account", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto createMe(@Valid @RequestBody CustomerBootstrapRequest req) {
        return customerService.createMyProfile(req);
    }

    @Operation(summary = "Update my profile", description = "Partially update the authenticated customer's profile fields.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated profile returned"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    @PatchMapping
    public CustomerDto patch(@Valid @RequestBody CustomerPatchRequest req) {
        return customerService.patchMe(req);
    }

    @Operation(summary = "Delete my account", description = "Permanently deletes the authenticated customer's account.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe() {
        customerService.deleteMe();
    }

    @Operation(summary = "Get my preferences", description = "Returns tag-based preference rows for the authenticated customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preferences returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not a customer account", content = @Content)
    })
    @GetMapping("/preferences")
    public List<CustomerPreferenceDto> getPreferences() {
        return customerPreferenceService.getMyPreferences();
    }

    @Operation(summary = "Save my preferences", description = "Replaces preference selections that drive AI suggestions and storefront personalization.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preferences saved"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not a customer account", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer profile missing", content = @Content)
    })
    @PutMapping("/preferences")
    public List<CustomerPreferenceDto> savePreferences(@RequestBody CustomerPreferenceSaveRequest request) {
        return customerPreferenceService.saveMyPreferences(request);
    }

}
