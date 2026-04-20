package com.sait.peelin.dto.v1;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StaffConversationDto(
        Integer id,
        UUID otherUserId,
        String otherUsername,
        String otherProfilePhotoPath,
        String otherRole,
        OffsetDateTime updatedAt,
        int unreadCount
) {}
