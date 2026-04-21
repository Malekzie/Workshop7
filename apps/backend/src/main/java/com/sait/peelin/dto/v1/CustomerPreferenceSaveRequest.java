// Contributor(s): Mason
// Main: Mason - Storefront or profile JSON DTO for catalog addresses and reviews.

package com.sait.peelin.dto.v1;

import com.sait.peelin.model.PreferenceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(name = "CustomerPreferenceSaveRequest", description = "Bulk replace payload for customer taste tags.")
@Data
public class CustomerPreferenceSaveRequest {
    @Schema(description = "Preference rows to persist.")
    private List<PreferenceEntry> preferences;

    @Schema(name = "PreferenceEntry", description = "One tag selection inside the save bundle.")
    @Data
    public static class PreferenceEntry {
        @Schema(description = "Tag id to score.")
        private Integer tagId;
        @Schema(description = "Preference direction enum.")
        private PreferenceType preferenceType;
    }
}
