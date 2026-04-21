// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Creates the customer profile for a user that registered without one.
 * Customer email is taken from the authenticated user account email.
 */
@Schema(name = "CustomerBootstrapRequest", description = "First-time profile and address after registration for customer role users.")
@Getter
@Setter
public class CustomerBootstrapRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Given name up to 50 characters.")
    @NotBlank
    @Size(max = 50)
    private String firstName;

    @Schema(description = "Optional single letter middle initial.")
    @Size(max = 1)
    @Pattern(regexp = "^$|^[A-Za-z]$", message = "Middle initial must be a single letter")
    private String middleInitial;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Family name up to 50 characters.")
    @NotBlank
    @Size(max = 50)
    private String lastName;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Primary phone up to 20 characters.")
    @NotBlank
    @Size(max = 20)
    private String phone;

    @Schema(description = "Optional business phone up to 20 characters.")
    @Size(max = 20)
    private String businessPhone;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Street line one up to 120 characters.")
    @NotBlank
    @Size(max = 120)
    private String addressLine1;

    @Schema(description = "Optional street line two up to 120 characters.")
    @Size(max = 120)
    private String addressLine2;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "City up to 120 characters.")
    @NotBlank
    @Size(max = 120)
    private String city;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Province or state up to 80 characters.")
    @NotBlank
    @Size(max = 80)
    private String province;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Postal or ZIP up to 10 characters.")
    @NotBlank
    @Size(max = 10)
    private String postalCode;
}
