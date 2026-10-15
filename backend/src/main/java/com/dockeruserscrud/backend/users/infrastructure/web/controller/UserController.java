package com.dockeruserscrud.backend.users.infrastructure.web.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.dockeruserscrud.backend.users.application.usecase.CreateUserUseCase;
import com.dockeruserscrud.backend.users.application.usecase.GetUserByIdUseCase;
import com.dockeruserscrud.backend.users.domain.model.PageResult;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.CreateUserRequest;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.PageResponse;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.UpdateUserRequest;
import com.dockeruserscrud.backend.users.infrastructure.web.dto.UserResponse;
import com.dockeruserscrud.backend.users.infrastructure.web.mapper.UserWebMapper;
import com.dockeruserscrud.backend.users.application.usecase.ListUsersUseCase;
import com.dockeruserscrud.backend.users.application.usecase.UpdateUserUseCase;
import com.dockeruserscrud.backend.users.application.usecase.DeleteUserUseCase;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(
        CreateUserUseCase createUserUseCase,
        GetUserByIdUseCase getUserByIdUseCase,
        ListUsersUseCase listUsersUseCase,
        UpdateUserUseCase updateUserUseCase,
        DeleteUserUseCase deleteUserUseCase
        ) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {
        User user = getUserByIdUseCase.execute(id);

        return ResponseEntity.ok(
                UserWebMapper.toResponse(user)
        );
    }

    @GetMapping
        public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "20") int size
        ) {
        PageResult<User> result =
                listUsersUseCase.execute(page, size);

        return ResponseEntity.ok(
                UserWebMapper.toPageResponse(result)
        );
        }

        @PutMapping("/{id}")
        public ResponseEntity<UserResponse> updateUser(
                @PathVariable Long id,
                @Valid @RequestBody UpdateUserRequest request
        ) {
        User updatedUser = updateUserUseCase.execute(
                UserWebMapper.toCommand(id, request)
        );

        return ResponseEntity.ok(
                UserWebMapper.toResponse(updatedUser)
        );
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUser(
                @PathVariable Long id
        ) {
        deleteUserUseCase.execute(id);

        return ResponseEntity.noContent().build();
        }


}