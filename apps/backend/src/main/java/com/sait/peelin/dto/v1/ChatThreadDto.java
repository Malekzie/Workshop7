package com.sait.peelin.dto.v1;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatThreadDto(
        Integer id,
        UUID customerUserId,
        UUID employeeUserId,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
