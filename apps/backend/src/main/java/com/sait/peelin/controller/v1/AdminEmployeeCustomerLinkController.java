// Contributor(s): Owen
// Main: Owen - Admin one-to-one link between employee and customer for staff discount eligibility.

package com.sait.peelin.controller.v1;

import com.sait.peelin.dto.v1.EmployeeCustomerLinkCreateRequest;
import com.sait.peelin.service.EmployeeCustomerLinkService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only create for employee-customer pairs at {@code /api/v1/admin/employee-customer-links}.
 */
@RestController
@RequestMapping("/api/v1/admin/employee-customer-links")
@RequiredArgsConstructor
@Tag(name = "Admin employee customer link", description = "One employee row paired to one customer row for staff discount eligibility.")
@SecurityRequirement(name = "bearer-jwt")
public class AdminEmployeeCustomerLinkController {

    private final EmployeeCustomerLinkService employeeCustomerLinkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create employee customer link", description = "Persists the one to one pairing used when staff pricing rules reference linked households.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Link created"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public void create(@Valid @RequestBody EmployeeCustomerLinkCreateRequest body) {
        employeeCustomerLinkService.createLinkAdmin(body.employeeId(), body.customerId());
    }
}
