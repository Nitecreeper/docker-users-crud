package com.dockeruserscrud.backend.users.application.usecase;

import com.dockeruserscrud.backend.users.application.command.UpdateUserCommand;
import com.dockeruserscrud.backend.users.application.exception.EmailAlreadyExistsException;
import com.dockeruserscrud.backend.users.application.exception.UserNotFoundException;
import com.dockeruserscrud.backend.users.domain.model.User;
import com.dockeruserscrud.backend.users.domain.port.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserUseCase {

    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User execute(UpdateUserCommand command) {
        User existingUser = userRepository
                .findById(command.id())
                .orElseThrow(
                        () -> new UserNotFoundException(command.id())
                );

        User updatedUser = new User(
                existingUser.id(),
                command.name(),
                command.email(),
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        boolean emailChanged = !existingUser
                .email()
                .equals(updatedUser.email());

        if (emailChanged
                && userRepository.existsByEmail(updatedUser.email())) {
            throw new EmailAlreadyExistsException(
                    updatedUser.email()
            );
        }

        return userRepository.save(updatedUser);
    }
}