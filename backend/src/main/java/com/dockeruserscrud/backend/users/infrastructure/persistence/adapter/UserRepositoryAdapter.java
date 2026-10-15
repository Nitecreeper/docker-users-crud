package com.dockeruserscrud.backend.users.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;
import com.dockeruserscrud.backend.users.infrastructure.persistence.entity.UserJpaEntity;
import com.dockeruserscrud.backend.users.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.dockeruserscrud.backend.users.infrastructure.persistence.repository.SpringDataUserJpaRepository;
import com.dockeruserscrud.backend.users.domain.model.PageQuery;
import com.dockeruserscrud.backend.users.domain.model.PageResult;

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
    public PageResult<User> findAll(PageQuery pageQuery) {
        PageRequest springPageRequest = PageRequest.of(
                pageQuery.page(),
                pageQuery.size(),
                Sort.by(Sort.Direction.ASC, "id")
        );

        Page<UserJpaEntity> entityPage =
                repository.findAll(springPageRequest);

        List<User> users = entityPage
                .getContent()
                .stream()
                .map(UserPersistenceMapper::toDomain)
                .toList();

        return new PageResult<>(
                users,
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.isFirst(),
                entityPage.isLast()
        );
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
