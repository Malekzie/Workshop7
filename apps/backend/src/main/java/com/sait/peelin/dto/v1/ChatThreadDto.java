// Contributor(s): Robbie
// Main: Robbie - Support chat thread summary for REST list and detail responses.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "ChatThreadDto", description = "Support thread header with customer and assignee snapshots.")
public record ChatThreadDto(
        @Schema(description = "Thread primary key.") Integer id,
        @Schema(description = "Customer user id when known.") UUID customerUserId,
        @Schema(description = "Customer display label for inbox UI.") String customerDisplayName,
        @Schema(description = "Customer login handle.") String customerUsername,
        @Schema(description = "Customer mailbox on file.") String customerEmail,
        @Schema(description = "Customer profile image URL when set.") String customerProfilePhotoPath,
        @Schema(description = "True while customer photo awaits moderation.") Boolean customerPhotoApprovalPending,
        @Schema(description = "Assigned staff user id when claimed.") UUID employeeUserId,
        @Schema(description = "Staff display label.") String employeeDisplayName,
        @Schema(description = "Staff login handle.") String employeeUsername,
        @Schema(description = "Staff profile image URL when set.") String employeeProfilePhotoPath,
        @Schema(description = "Workflow status string such as open or closed.") String status,
        @Schema(description = "Routing category code.") String category,
        @Schema(description = "Instant the thread was created.") OffsetDateTime createdAt,
        @Schema(description = "Last activity instant.") OffsetDateTime updatedAt,
        @Schema(description = "Instant the thread moved to closed when applicable.") OffsetDateTime closedAt
) {}
