package com.dockeruserscrud.backend.users.infrastructure.web.controller;

import com.dockeruserscrud.backend.users.application.usecase.CreateUserUseCase;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.CreateUserRequest;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.UserResponse;
import com.dockeruserscrud.backend.users.infrastructure.web.mapper.UserWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        User createdUser = createUserUseCase.execute(
                UserWebMapper.toCommand(request)
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(UserWebMapper.toResponse(createdUser));
    }
}