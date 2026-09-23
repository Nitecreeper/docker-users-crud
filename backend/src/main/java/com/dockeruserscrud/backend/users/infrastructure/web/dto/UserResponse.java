package com.dockeruserscrud.backend.users.infrastructure.web.dto;

import java.time.Instant;

public record UserResponse(
        Long id,
        String name,
        String email,
        Instant createdAt,
        Instant updatedAt
) {
}