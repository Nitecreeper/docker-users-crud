package com.dockeruserscrud.backend.users.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dockeruserscrud.backend.users.infrastructure.persistence.entity.UserJpaEntity;

public interface SpringDataUserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);
    
}
