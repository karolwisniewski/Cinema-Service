package com.app.persistence.repositories.repository.impl;

import com.app.persistence.model.Movie;
import com.app.persistence.repositories.repository.MovieRepository;
import com.app.persistence.repositories.repository.criteria.MovieCriteria;
import com.app.persistence.repositories.repository.generic.AbstractCrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieRepositoryImpl extends AbstractCrudRepository<Movie, Integer> implements MovieRepository {

    @Override
    public List<Movie> findByMovieCriteria(MovieCriteria movieCriteria) {
        List<String> criteriaBuilder = new ArrayList<>();

        if (Objects.nonNull(movieCriteria.getTitle()) && !movieCriteria.getTitle().isEmpty()) {
            criteriaBuilder.add("title = '" + movieCriteria.getTitle() + "'");
        }

        if (Objects.nonNull(movieCriteria.getCategory())) {
            criteriaBuilder.add("category = '" + movieCriteria.getCategory() + "'");
        }

        if (Objects.nonNull(movieCriteria.getDateFrom())
                && Objects.nonNull(movieCriteria.getDateTo())
                && movieCriteria.getDateFrom().compareTo(movieCriteria.getDateTo()) <= 0) {
            criteriaBuilder.add("display_since between '" + movieCriteria.getDateFrom()  + "' and '" + movieCriteria.getDateTo() + "'");
        }

        String criteria = criteriaBuilder.stream().collect(Collectors.joining(" and "));
        var FIND_BY_CRITERIA_SQL = "select * from movies " + (!criteria.isEmpty() ? " where " + criteria : "") +";";
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_BY_CRITERIA_SQL)
                .mapToBean(Movie.class)
                .list());
    }

    @Override
    public Optional<Movie> doesExist(Movie movie) {
        var FIND_ROOM_IF_EXIST = """
                select * from movies 
                where title = :title
                and category = :category;
                """;
        return jdbi.withHandle(handle ->
                handle.createQuery(FIND_ROOM_IF_EXIST)
                        .bind("title", movie.getTitle())
                        .bind("category", movie.getCategory())
                        .bind("display_since", movie.getDisplaySince())
                        .bind("display_to", movie.getDisplayTo())
                        .mapToBean(Movie.class)
                        .findFirst());
    }



}
