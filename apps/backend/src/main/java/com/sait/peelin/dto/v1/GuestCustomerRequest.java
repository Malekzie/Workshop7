// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import com.sait.peelin.validation.GuestContactValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "GuestCustomerRequest", description = "Guest identity and address used when JWT checkout is not available.")
@Getter
@Setter
@GuestContactValid
public class GuestCustomerRequest {

    @Schema(description = "Given name for the receipt.")
    @Size(max = 50)
    private String firstName;

    @Schema(description = "Single letter middle initial when supplied.")
    @Size(max = 1)
    private String middleInitial;

    @Schema(description = "Family name for the receipt.")
    @Size(max = 50)
    private String lastName;

    @Schema(description = "Optional when email is set. Required with ten or more digits when email is blank per validation rules.")
    @Size(max = 20)
    private String phone;

    @Schema(description = "Alternate business line when needed.")
    @Size(max = 20)
    private String businessPhone;

    @Schema(description = "Optional when phone is set. Required when phone is blank per validation rules.")
    @Size(max = 254)
    private String email;

    @Schema(description = "Street line one for guest delivery.")
    @Size(max = 120)
    private String addressLine1;

    @Schema(description = "Street line two for apartment or suite text.")
    @Size(max = 120)
    private String addressLine2;

    @Schema(description = "City or town name.")
    @Size(max = 120)
    private String city;

    @Schema(description = "Province or state label.")
    @Size(max = 80)
    private String province;

    @Schema(description = "Postal or ZIP code string.")
    @Size(max = 10)
    private String postalCode;
}
