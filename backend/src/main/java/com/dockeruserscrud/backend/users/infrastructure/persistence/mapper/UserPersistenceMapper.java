package com.dockeruserscrud.backend.users.infrastructure.persistence.mapper;

import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.infrastructure.persistence.entity.UserJpaEntity;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
        // Evita instanciar esta clase utilitaria.
    }

    public static UserJpaEntity toNewEntity(User user) {
        return new UserJpaEntity(
                user.name(),
                user.email()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static void updateEntity(
            User source,
            UserJpaEntity target
    ) {
        target.setName(source.name());
        target.setEmail(source.email());
    }
}
