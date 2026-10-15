package com.dockeruserscrud.backend.users.application.usecase;

import com.dockeruserscrud.backend.users.application.command.CreateUserCommand;
import com.dockeruserscrud.backend.users.application.exception.EmailAlreadyExistsException;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateUserUseCaseTest {

    private UserRepository userRepository;
    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        createUserUseCase = new CreateUserUseCase(userRepository);
    }

    @Test
    void shouldSaveNormalizedUserAndReturnPersistedUser() {
        // Arrange
        CreateUserCommand command = new CreateUserCommand(
                "  Leo  ",
                "  LEO@EXAMPLE.COM  "
        );

        Instant timestamp = Instant.parse("2026-10-15T00:00:00Z");

        User persistedUser = new User(
                1L,
                "Leo",
                "leo@example.com",
                timestamp,
                timestamp
        );

        when(userRepository.existsByEmail("leo@example.com"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenReturn(persistedUser);

        // Act
        User result = createUserUseCase.execute(command);

        // Assert
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        assertEquals("Leo", userToSave.name());
        assertEquals("leo@example.com", userToSave.email());
        assertNull(userToSave.id());
        assertNull(userToSave.createdAt());
        assertNull(userToSave.updatedAt());

        verify(userRepository).existsByEmail("leo@example.com");
        assertSame(persistedUser, result);
    }

    @Test
    void shouldRejectDuplicateEmailWithoutSaving() {
        // Arrange
        CreateUserCommand command = new CreateUserCommand(
                "Leo",
                "  LEO@EXAMPLE.COM  "
        );

        when(userRepository.existsByEmail("leo@example.com"))
                .thenReturn(true);

        // Act + Assert
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> createUserUseCase.execute(command)
        );

        verify(userRepository).existsByEmail("leo@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
}