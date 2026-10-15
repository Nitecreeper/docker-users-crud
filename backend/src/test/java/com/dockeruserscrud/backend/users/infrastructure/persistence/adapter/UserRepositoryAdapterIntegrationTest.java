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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@Import(PostgresTestConfiguration.class)
@Transactional
class UserRepositoryAdapterIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndRetrieveUser() {
        // Arrange
        User newUser = User.create(
                "Leo",
                "leo@example.com"
        );

        // Act
        User savedUser = userRepository.save(newUser);

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
}