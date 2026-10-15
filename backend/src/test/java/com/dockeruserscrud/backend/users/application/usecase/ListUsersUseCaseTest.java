package com.dockeruserscrud.backend.users.application.usecase;

import com.dockeruserscrud.backend.users.domain.model.PageQuery;
import com.dockeruserscrud.backend.users.domain.model.PageResult;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ListUsersUseCaseTest {

    private UserRepository userRepository;
    private ListUsersUseCase listUsersUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        listUsersUseCase = new ListUsersUseCase(userRepository);
    }

    @Test
    void shouldReturnRequestedPage() {
        // Arrange
        Instant timestamp = Instant.parse("2026-10-15T00:00:00Z");

        User user = new User(
                3L,
                "Leo",
                "leo@example.com",
                timestamp,
                timestamp
        );

        PageResult<User> expectedPage = new PageResult<>(
                List.of(user),
                3L,
                2,
                1,
                2,
                false,
                true
        );

        when(userRepository.findAll(new PageQuery(1, 2)))
                .thenReturn(expectedPage);

        // Act
        PageResult<User> result = listUsersUseCase.execute(1, 2);

        // Assert
        assertSame(expectedPage, result);
    }

    @Test
    void shouldReturnEmptyPageWhenThereAreNoUsers() {
        // Arrange
        PageResult<User> emptyPage = new PageResult<>(
                List.of(),
                0L,
                0,
                0,
                20,
                true,
                true
        );

        when(userRepository.findAll(new PageQuery(0, 20)))
                .thenReturn(emptyPage);

        // Act
        PageResult<User> result = listUsersUseCase.execute(0, 20);

        // Assert
        assertSame(emptyPage, result);
    }

    @Test
    void shouldRejectNegativePageWithoutCallingRepository() {
        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> listUsersUseCase.execute(-1, 20)
        );

        verifyNoInteractions(userRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 101})
    void shouldRejectInvalidSizeWithoutCallingRepository(int size) {
        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> listUsersUseCase.execute(0, size)
        );

        verifyNoInteractions(userRepository);
    }
}