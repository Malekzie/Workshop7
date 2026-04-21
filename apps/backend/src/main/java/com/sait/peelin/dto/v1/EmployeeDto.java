// Contributor(s): Robbie
// Main: Robbie - Employee profile projection for self-service and directory APIs.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "EmployeeDto", description = "Employee directory row with nested address when loaded.")
public record EmployeeDto(
        @Schema(description = "Employee profile id.") UUID id,
        @Schema(description = "Backing user id.") UUID userId,
        @Schema(description = "Primary bakery assignment id.") Integer bakeryId,
        @Schema(description = "Login handle.") String username,
        @Schema(description = "Given name.") String firstName,
        @Schema(description = "Single letter middle initial when set.") String middleInitial,
        @Schema(description = "Family name.") String lastName,
        @Schema(description = "Job title text.") String position,
        @Schema(description = "Primary phone.") String phone,
        @Schema(description = "Alternate business phone.") String businessPhone,
        @Schema(description = "Work mailbox.") String workEmail,
        @Schema(description = "Linked address id when present.") Integer addressId,
        @Schema(description = "Expanded postal address when requested.") AddressDto address,
        @Schema(description = "Profile image URL when set.") String profilePhotoPath,
        @Schema(description = "True while profile photo awaits approval.") boolean photoApprovalPending,
        @Schema(description = "True when discount link rules may apply to this employee.") boolean customerLinkEligible
) {}
