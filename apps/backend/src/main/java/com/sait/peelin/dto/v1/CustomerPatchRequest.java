// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "CustomerPatchRequest", description = "Sparse admin or self-service update for customer fields.")
@Data
public class CustomerPatchRequest {
    @Schema(description = "Manual points adjustment when staff edits ledgers.")
    private Integer rewardBalance;
    @Schema(description = "New given name when provided.")
    private String firstName;

    @Schema(description = "Middle initial or blank.")
    @Size(max = 1)
    @Pattern(regexp = "^$|^[A-Za-z]$", message = "Middle initial must be a single letter")
    private String middleInitial;
    @Schema(description = "New family name when provided.")
    private String lastName;
    @Schema(description = "New username when provided.")
    private String username;
    @Schema(description = "Primary phone when provided.")
    private String phone;
    @Schema(description = "Business phone when provided.")
    private String businessPhone;
    @Schema(description = "Primary email when provided.")
    private String email;
    @Schema(description = "Address id pointer when provided.")
    private Integer addressId;
    @Schema(description = "Inline address upsert when replacing street data.")
    @Valid
    private AddressUpsertRequest address;
    @Schema(description = "Reward tier id override when provided.")
    private Integer rewardTierId;
    @Schema(description = "Photo moderation flag override when provided.")
    private Boolean photoApprovalPending;
}
