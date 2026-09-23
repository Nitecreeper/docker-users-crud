package com.dockeruserscrud.backend.users.application.command;

public record CreateUserCommand (
        String name,
        String email
){    
}
