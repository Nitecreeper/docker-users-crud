package com.dockeruserscrud.backend.users.infrastructure.web.mapper;

import com.dockeruserscrud.backend.users.application.command.CreateUserCommand;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.CreateUserRequest;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.UserResponse;

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
}