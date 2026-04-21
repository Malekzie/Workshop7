// Contributor(s): Robbie
// Main: Robbie - Admin or dashboard JSON DTO for staff tools.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(name = "UserActivePatchRequest", description = "Toggle whether a user may authenticate.")
@Data
public class UserActivePatchRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "True allows login. False blocks authentication.")
    @NotNull
    private Boolean active;
}
