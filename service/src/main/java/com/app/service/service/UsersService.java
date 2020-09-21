package com.app.service.service;

import com.app.persistence.model.User;
import com.app.persistence.dto.CreateUserDto;
import com.app.persistence.repositories.repository.UserRepository;
import com.app.service.exceptions.UsersServiceException;
import com.app.service.mapper.Mappers;
import com.app.service.validator.CreateUserDtoValidator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UsersService {
    private final UserRepository userRepository;

    public String register(CreateUserDto createUserDto) {
        var createUserValidator = new CreateUserDtoValidator();
        var errors = createUserValidator.validate(createUserDto);
        if (!errors.isEmpty()) {
            String errorMessage = errors.entrySet().stream()
                    .map(error -> error.getKey() + " -> " + error.getValue())
                    .collect(Collectors.joining("\n"));

            throw new UsersServiceException("create user dto validation error. " + errorMessage);
        }

        var user = Mappers.fromCreateUserDtoToUser(createUserDto);
        user.setPassword(Encryption.encrypt(user.getPassword()));

        if(userRepository.isEmailAlreadyTaken(user.getEmail())){
            throw new UsersServiceException("Given email address is already taken");
        }
        if(userRepository.isUsernameAlreadyTaken(user.getUsername())){
            throw new UsersServiceException("Given username is already taken");
        }

        return userRepository
                .add(user)
                .map(User::getUsername)
                .orElseThrow(() -> new UsersServiceException("cannot insert user"));
    }
}
