// Contributor(s): Owen
// Main: Owen - Admin request body for employee-customer discount link.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "EmployeeCustomerLinkCreateRequest", description = "Pairs one employee user to one customer row for staff pricing rules.")
public record EmployeeCustomerLinkCreateRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Employee profile row id in the pair.")
        @NotNull UUID employeeId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Customer profile row id in the pair.")
        @NotNull UUID customerId
) {}
