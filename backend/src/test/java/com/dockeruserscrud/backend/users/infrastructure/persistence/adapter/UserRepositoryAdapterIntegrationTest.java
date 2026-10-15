package com.dockeruserscrud.backend.users.infrastructure.persistence.adapter;

import com.dockeruserscrud.backend.support.PostgresTestConfiguration;
import com.dockeruserscrud.backend.users.domain.model.PageQuery;
import com.dockeruserscrud.backend.users.domain.model.PageResult;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.time.OffsetDateTime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@Import(PostgresTestConfiguration.class)
@Transactional
class UserRepositoryAdapterIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
        private EntityManager entityManager;

        @Autowired
        private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSaveAndRetrieveUser() {
        // Arrange
        User newUser = User.create(
                "Leo",
                "leo@example.com"
        );

        // Act
        User savedUser = userRepository.save(newUser);

        entityManager.clear();

        User retrievedUser = userRepository
                .findById(savedUser.id())
                .orElseThrow();

        // Assert
        assertNotNull(savedUser.id());
        assertNotNull(savedUser.createdAt());
        assertNotNull(savedUser.updatedAt());

        assertEquals(savedUser.id(), retrievedUser.id());
        assertEquals("Leo", retrievedUser.name());
        assertEquals("leo@example.com", retrievedUser.email());
    }

    @Test
    void shouldReturnSecondPageOrderedById() {
        // Arrange
        User first = userRepository.save(
                User.create("First", "first@example.com")
        );

        User second = userRepository.save(
                User.create("Second", "second@example.com")
        );

        User third = userRepository.save(
                User.create("Third", "third@example.com")
        );

        // Act
        PageResult<User> result = userRepository.findAll(
                new PageQuery(1, 2)
        );

        // Assert
        assertEquals(3L, result.totalElements());
        assertEquals(2, result.totalPages());
        assertEquals(1, result.page());
        assertEquals(2, result.size());
        assertFalse(result.first());
        assertTrue(result.last());

        List<Long> returnedIds = result.content()
                .stream()
                .map(User::id)
                .toList();

        assertEquals(List.of(third.id()), returnedIds);
        assertTrue(first.id() < second.id());
        assertTrue(second.id() < third.id());
    }

    @Test
        void shouldUpdateUserAndRefreshUpdatedAt() {
        // Arrange
        User savedUser = userRepository.save(
                User.create("Leo", "leo@example.com")
        );

        Instant originalCreatedAt =
                Instant.parse("2026-01-01T10:00:00Z");

        Instant previousUpdatedAt =
                Instant.parse("2026-01-02T10:00:00Z");

        jdbcTemplate.update(
                """
                UPDATE users
                SET created_at = ?, updated_at = ?
                WHERE id = ?
                """,
                OffsetDateTime.parse("2026-01-01T10:00:00Z"),
                OffsetDateTime.parse("2026-01-02T10:00:00Z"),
                savedUser.id()
        );

        entityManager.clear();

        User existingUser = userRepository
                .findById(savedUser.id())
                .orElseThrow();

        User changes = new User(
                existingUser.id(),
                "Leonardo",
                "leonardo@example.com",
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        // Act
        User updatedUser = userRepository.save(changes);

        entityManager.clear();

        User retrievedUser = userRepository
                .findById(savedUser.id())
                .orElseThrow();

        // Assert
        assertEquals(savedUser.id(), retrievedUser.id());
        assertEquals("Leonardo", retrievedUser.name());
        assertEquals("leonardo@example.com", retrievedUser.email());

        assertEquals(originalCreatedAt, retrievedUser.createdAt());

        assertTrue(
                updatedUser.updatedAt().isAfter(previousUpdatedAt)
        );

        assertTrue(
                retrievedUser.updatedAt().isAfter(previousUpdatedAt)
        );
        }

        @Test
        void shouldDeletePersistedUser() {
        // Arrange
        User savedUser = userRepository.save(
                User.create("Delete test", "delete@example.com")
        );

        // Act
        userRepository.deleteById(savedUser.id());

        entityManager.flush();
        entityManager.clear();

        // Assert
        assertTrue(
                userRepository.findById(savedUser.id()).isEmpty()
        );
        }

        @Test
        void shouldRejectDuplicateEmailAtDatabaseLevel() {
        // Arrange
        userRepository.save(
                User.create("First user", "duplicate@example.com")
        );

        User duplicateUser = User.create(
                "Second user",
                "duplicate@example.com"
        );

        // Act + Assert
        assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.save(duplicateUser)
        );
        }
}