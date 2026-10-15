package com.dockeruserscrud.backend.users.application.command;

public record UpdateUserCommand(
        Long id,
        String name,
        String email
) {
}