package com.dockeruserscrud.backend.users.infrastructure.persistence.repository;

import com.dockeruserscrud.backend.users.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserJpaRepository
        extends JpaRepository<UserJpaEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);
}