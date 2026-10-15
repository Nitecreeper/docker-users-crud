package com.dockeruserscrud.backend.users.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dockeruserscrud.backend.users.application.exception.UserNotFoundException;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteUserUseCaseTest {

    private UserRepository userRepository;
    private DeleteUserUseCase deleteUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        deleteUserUseCase = new DeleteUserUseCase(userRepository);
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        // Arrange: preparar el escenario.
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act: ejecutar la operación.
        deleteUserUseCase.execute(userId);

        // Assert: comprobar el comportamiento.
        verify(userRepository).deleteById(userId);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        // Arrange
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act + Assert
        assertThrows(
                UserNotFoundException.class,
                () -> deleteUserUseCase.execute(userId)
        );

        verify(userRepository, never()).deleteById(anyLong());
    }
}