package com.dockeruserscrud.backend.users.application.usecase;

import com.dockeruserscrud.backend.users.application.exception.UserNotFoundException;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUserByIdUseCaseTest {

    private UserRepository userRepository;
    private GetUserByIdUseCase getUserByIdUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        getUserByIdUseCase = new GetUserByIdUseCase(userRepository);
    }

    @Test
    void shouldReturnUserWhenUserExists() {
        // Arrange
        Instant timestamp = Instant.parse("2026-10-15T00:00:00Z");

        User expectedUser = new User(
                1L,
                "Leo",
                "leo@example.com",
                timestamp,
                timestamp
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(expectedUser));

        // Act
        User result = getUserByIdUseCase.execute(1L);

        // Assert
        assertSame(expectedUser, result);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                UserNotFoundException.class,
                () -> getUserByIdUseCase.execute(999L)
        );
    }
}