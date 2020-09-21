package com.app.persistence.repositories.repository;

import com.app.persistence.model.User;
import com.app.persistence.repositories.repository.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<String> getEmail(Integer userId);
    List<String> getEmailList(Integer movieId);
    List<String> getAllEmails();
    Optional<User> findByUsername(String username);
    boolean isEmailAlreadyTaken(String email);
    boolean isUsernameAlreadyTaken(String username);
}
