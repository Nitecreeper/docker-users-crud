package com.dockeruserscrud.backend.users.domain.model;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean first,
        boolean last
) {

    public PageResult {
        content = List.copyOf(content);
    }
}