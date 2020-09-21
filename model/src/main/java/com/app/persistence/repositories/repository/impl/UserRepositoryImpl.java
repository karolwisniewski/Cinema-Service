package com.app.persistence.repositories.repository.impl;

import com.app.persistence.model.User;
import com.app.persistence.repositories.repository.UserRepository;
import com.app.persistence.repositories.repository.generic.AbstractCrudRepository;

import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl extends AbstractCrudRepository<User, Integer> implements UserRepository {

    @Override
    public Optional<String> getEmail(Integer userId) {
        var GET_EMAIL = """
                select email from users 
                where id = :userId;
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(GET_EMAIL)
                .bind("userId", userId)
                .map((rs, ctx) -> rs.getString("email"))
                .findFirst());
    }

    @Override
    public List<String> getEmailList(Integer movieId) {
        var FIND_EMAILS = """
                select email from users 
                left join favourites on users.id = favourites.user_id
                left join movies on movies.id = favourites.movie_id
                where movies.id = :movieId;
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_EMAILS)
                .bind("movieId", movieId)
                .map((rs, ctx) -> rs.getString("email"))
                .list());
    }

    @Override
    public List<String> getAllEmails() {
        var FIND_ALL_EMAILS = """
                select distinct email from users;
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_ALL_EMAILS)
                .map((rs, crx) -> rs.getString("email"))
                .list());
    }

    @Override
    public Optional<User> findByUsername(String username) {
        var FIND_BY_USERNAME = """
                select * from users where username = :username;
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_BY_USERNAME)
                .bind("username", username)
                .mapToBean(User.class)
                .findFirst());
    }

    @Override
    public boolean isEmailAlreadyTaken(String email) {
        var FIND_BY_EMAIL = """
                select email from users where  email = :email;
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_BY_EMAIL)
                .bind("email", email)
                .map((rs, ctx) -> rs.getString("email"))
                .findFirst()
                .isPresent());
    }

    @Override
    public boolean isUsernameAlreadyTaken(String username) {
        var FIND_BY_USERNAME_OR_EMAIL = """
                select username from users where username = :username;
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_BY_USERNAME_OR_EMAIL)
                .bind("username", username)
                .map((rs, ctx) -> rs.getString("username"))
                .findFirst()
                .isPresent());
    }
}
