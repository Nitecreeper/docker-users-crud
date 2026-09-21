package com.dockeruserscrud.backend.users.domain.model;

import java.time.Instant;
import java.util.Locale;

public record User(
        Long id,
        String name,
        String email,
        Instant createdAt,
        Instant updatedAt
) {

    public User {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name must not be blank");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("User email must not be blank");
        }

        name = name.trim();
        email = email.trim().toLowerCase(Locale.ROOT);
    }

    public static User create(String name, String email) {
        return new User(
                null,
                name,
                email,
                null,
                null
        );
    }
}