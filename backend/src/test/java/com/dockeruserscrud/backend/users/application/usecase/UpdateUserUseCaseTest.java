package com.dockeruserscrud.backend.users.application.usecase;

import com.dockeruserscrud.backend.users.application.command.UpdateUserCommand;
import com.dockeruserscrud.backend.users.application.exception.EmailAlreadyExistsException;
import com.dockeruserscrud.backend.users.application.exception.UserNotFoundException;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateUserUseCaseTest {

    private UserRepository userRepository;
    private UpdateUserUseCase updateUserUseCase;

    private User existingUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        updateUserUseCase = new UpdateUserUseCase(userRepository);

        Instant createdAt = Instant.parse("2026-10-01T12:00:00Z");

        existingUser = new User(
                1L,
                "Leo",
                "leo@example.com",
                createdAt,
                createdAt
        );
    }

    @Test
    void shouldUpdateUserWhenNewEmailIsAvailable() {
        // Arrange
        UpdateUserCommand command = new UpdateUserCommand(
                1L,
                "  Leonardo  ",
                "  LEONARDO@EXAMPLE.COM  "
        );

        User persistedUser = new User(
                existingUser.id(),
                "Leonardo",
                "leonardo@example.com",
                existingUser.createdAt(),
                Instant.parse("2026-10-15T00:00:00Z")
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByEmail("leonardo@example.com"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenReturn(persistedUser);

        // Act
        User result = updateUserUseCase.execute(command);

        // Assert
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        assertEquals(existingUser.id(), userToSave.id());
        assertEquals("Leonardo", userToSave.name());
        assertEquals("leonardo@example.com", userToSave.email());
        assertEquals(existingUser.createdAt(), userToSave.createdAt());

        verify(userRepository).existsByEmail("leonardo@example.com");
        assertSame(persistedUser, result);
    }

    @Test
    void shouldUpdateWithoutCheckingDuplicatesWhenEmailIsUnchanged() {
        // Arrange
        UpdateUserCommand command = new UpdateUserCommand(
                1L,
                "Leonardo",
                "  LEO@EXAMPLE.COM  "
        );

        User persistedUser = new User(
                existingUser.id(),
                "Leonardo",
                "leo@example.com",
                existingUser.createdAt(),
                Instant.parse("2026-10-15T00:00:00Z")
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.save(any(User.class)))
                .thenReturn(persistedUser);

        // Act
        User result = updateUserUseCase.execute(command);

        // Assert
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        assertEquals("Leonardo", userCaptor.getValue().name());
        assertEquals(
                existingUser.email(),
                userCaptor.getValue().email()
        );

        verify(userRepository, never()).existsByEmail(anyString());
        assertSame(persistedUser, result);
    }

    @Test
    void shouldRejectEmailAlreadyUsedByAnotherUser() {
        // Arrange
        UpdateUserCommand command = new UpdateUserCommand(
                1L,
                "Leonardo",
                "other@example.com"
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByEmail("other@example.com"))
                .thenReturn(true);

        // Act + Assert
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> updateUserUseCase.execute(command)
        );

        verify(userRepository).existsByEmail("other@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectUpdateWhenUserDoesNotExist() {
        // Arrange
        UpdateUserCommand command = new UpdateUserCommand(
                999L,
                "Leonardo",
                "leonardo@example.com"
        );

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                UserNotFoundException.class,
                () -> updateUserUseCase.execute(command)
        );

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}