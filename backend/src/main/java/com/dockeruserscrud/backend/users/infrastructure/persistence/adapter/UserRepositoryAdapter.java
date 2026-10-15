package com.dockeruserscrud.backend.users.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;
import com.dockeruserscrud.backend.users.infrastructure.persistence.entity.UserJpaEntity;
import com.dockeruserscrud.backend.users.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.dockeruserscrud.backend.users.infrastructure.persistence.repository.SpringDataUserJpaRepository;

import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserJpaRepository repository;

    public UserRepositoryAdapter(SpringDataUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity;

        if (user.id() == null) {
            entity = UserPersistenceMapper.toNewEntity(user);
        } else {
            entity = repository.findById(user.id())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "User not found: " + user.id()
                    ));

            UserPersistenceMapper.updateEntity(user, entity);
        }

        UserJpaEntity savedEntity = repository.save(entity);
        return UserPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository
                .findAllByOrderByIdAsc()
                .stream()
                .map(UserPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
}
