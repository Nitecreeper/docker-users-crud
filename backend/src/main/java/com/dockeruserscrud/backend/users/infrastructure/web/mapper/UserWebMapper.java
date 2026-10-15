package com.dockeruserscrud.backend.users.infrastructure.web.mapper;

import com.dockeruserscrud.backend.users.application.command.CreateUserCommand;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.CreateUserRequest;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.UserResponse;
import com.dockeruserscrud.backend.users.domain.model.PageResult;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.PageResponse;

import java.util.List;

public final class UserWebMapper {

    private UserWebMapper() {
        // Evita instanciar esta clase utilitaria.
    }

    public static CreateUserCommand toCommand(CreateUserRequest request) {
        return new CreateUserCommand(
                request.name(),
                request.email()
        );
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.id(),
                user.name(),
                user.email(),
                user.createdAt(),
                user.updatedAt()
        );
    }

    public static List<UserResponse> toResponseList(List<User> users) {
    return users
            .stream()
            .map(UserWebMapper::toResponse)
            .toList();
    }

    public static PageResponse<UserResponse> toPageResponse(
            PageResult<User> pageResult
    ) {
        List<UserResponse> content = pageResult
                .content()
                .stream()
                .map(UserWebMapper::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                pageResult.totalElements(),
                pageResult.totalPages(),
                pageResult.page(),
                pageResult.size(),
                pageResult.first(),
                pageResult.last()
        );
    }
}