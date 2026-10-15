package com.dockeruserscrud.backend.users.domain.model;

public record PageQuery(
    int page,
    int size
) {
    public PageQuery {
        if (page < 0) {
            throw new IllegalArgumentException(
                "Page number must be greater than or equal to zero"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                "Page size must be between 1 and 100"
            );
        }
    }
}