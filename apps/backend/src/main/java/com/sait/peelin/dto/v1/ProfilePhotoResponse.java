// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "ProfilePhotoResponse", description = "Paths returned after uploading a profile image.")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePhotoResponse {
    @Schema(description = "Public URL or storage key for the new photo.")
    private String profilePhotoPath;
    @Schema(description = "True while staff moderation has not approved the upload.")
    private boolean photoApprovalPending;
}
