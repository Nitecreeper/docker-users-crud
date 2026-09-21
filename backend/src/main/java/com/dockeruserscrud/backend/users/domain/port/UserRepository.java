package com.dockeruserscrud.backend.users.domain.port;

import java.util.List;
import java.util.Optional;

import com.dockeruserscrud.backend.users.domain.model.User;

public interface UserRepository {

    User save(User user);
    
    Optional<User> findById(Long id);

    List<User> findAll();

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    void deleteById(Long id);
    
}
