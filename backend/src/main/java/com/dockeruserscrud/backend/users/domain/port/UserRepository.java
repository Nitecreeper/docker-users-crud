package com.dockeruserscrud.backend.users.domain.port;

import java.util.Optional;

import com.dockeruserscrud.backend.users.domain.model.PageQuery;
import com.dockeruserscrud.backend.users.domain.model.PageResult;
import com.dockeruserscrud.backend.users.domain.model.User;

public interface UserRepository {

    User save(User user);
    
    Optional<User> findById(Long id);

    PageResult<User> findAll(PageQuery pageQuery);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    void deleteById(Long id);
    
}
