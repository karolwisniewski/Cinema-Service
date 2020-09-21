package com.app.service.service;

import com.app.persistence.model.User;
import com.app.persistence.model.enums.Role;
import com.app.persistence.repositories.repository.UserRepository;
import com.app.persistence.dto.AuthenticationDto;
import com.app.service.exceptions.AuthenticationServiceException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private User authenticated;

    public User getAuthenticated() {
        return authenticated;
    }

    public String login(AuthenticationDto authenticationDto) {
        if (authenticationDto == null) {
            throw new AuthenticationServiceException("authentication dto data are not correct");
        }

        authenticated = userRepository
                .findByUsername(authenticationDto.getUsername())
                .flatMap(user -> {
                    return Encryption.checkPassword(authenticationDto.getPassword(), user.getPassword()) ? Optional.of(user) : Optional.empty();
                })
                .orElseThrow(() -> new AuthenticationServiceException("authentication failed"));
        return authenticated.getUsername();
    }

    public String logout() {
        String loggedOutUsername = authenticated.getUsername();
        authenticated = null;
        return loggedOutUsername;
    }

    private void checkRole(Role role) {
        if (authenticated == null) {
            throw new AuthenticationServiceException("LOG IN TO GET ACCESS FOR THIS RESOURCE");
        }
        if (!authenticated.getUserRole().equals(role)) {
            throw new AuthenticationServiceException("ACCESS DENIED!");
        }
    }

    public void adminAccess() {
        checkRole(Role.ADMIN);
    }

    public void userAccess() {
        checkRole(Role.USER);
    }
}
