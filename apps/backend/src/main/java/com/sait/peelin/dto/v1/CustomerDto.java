// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "CustomerDto", description = "Customer profile with loyalty tier snapshot and nested address.")
public record CustomerDto(
        @Schema(description = "Customer profile id.") UUID id,
        @Schema(description = "Backing user id when registered.") UUID userId,
        @Schema(description = "Login handle.") String username,
        @Schema(description = "Current loyalty tier id.") Integer rewardTierId,
        @Schema(description = "Tier marketing label.") String rewardTierName,
        @Schema(description = "Percent discount for the tier as a whole number such as five for five percent.") BigDecimal rewardTierDiscountPercent,
        @Schema(description = "Given name.") String firstName,
        @Schema(description = "Single letter middle initial when set.") String middleInitial,
        @Schema(description = "Family name.") String lastName,
        @Schema(description = "Primary phone.") String phone,
        @Schema(description = "Business phone when set.") String businessPhone,
        @Schema(description = "Primary mailbox.") String email,
        @Schema(description = "Lifetime points balance.") int rewardBalance,
        @Schema(description = "Saved address id when set.") Integer addressId,
        @Schema(description = "Expanded postal address when loaded.") AddressDto address,
        @Schema(description = "Profile image URL when set.") String profilePhotoPath,
        @Schema(description = "True while profile photo awaits approval.") boolean photoApprovalPending,
        @Schema(description = "True when linked employee discount rules apply at checkout.") boolean employeeDiscountEligible
) {}
