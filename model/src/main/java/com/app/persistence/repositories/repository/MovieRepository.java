package com.app.persistence.repositories.repository;

import com.app.persistence.model.Movie;
import com.app.persistence.repositories.repository.criteria.MovieCriteria;
import com.app.persistence.repositories.repository.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends CrudRepository<Movie, Integer> {
    List<Movie> findByMovieCriteria(MovieCriteria movieCriteria);
    Optional<Movie> doesExist(Movie movie);

}
