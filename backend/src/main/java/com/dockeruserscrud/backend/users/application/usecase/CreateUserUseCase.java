package com.dockeruserscrud.backend.users.application.usecase;

import com.dockeruserscrud.backend.users.application.command.CreateUserCommand;
import com.dockeruserscrud.backend.users.application.exception.EmailAlreadyExistsException;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUserUseCase {

    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User execute(CreateUserCommand command) {
        User user = User.create(
                command.name(),
                command.email()
        );

        if (userRepository.existsByEmail(user.email())) {
            throw new EmailAlreadyExistsException(user.email());
        }

        return userRepository.save(user);
    }
}