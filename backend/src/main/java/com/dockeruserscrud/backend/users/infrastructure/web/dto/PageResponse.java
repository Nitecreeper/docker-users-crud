package com.dockeruserscrud.backend.users.infrastructure.web.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean first,
        boolean last
) {

    public PageResponse {
        content = List.copyOf(content);
    }
}