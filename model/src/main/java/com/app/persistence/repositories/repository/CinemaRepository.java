package com.app.persistence.repositories.repository;

import com.app.persistence.model.Cinema;
import com.app.persistence.repositories.repository.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CinemaRepository extends CrudRepository<Cinema, Integer> {
    List<String> findAllCities();
    List<Cinema> findCinemasInCity(String city);
    Optional<Cinema> doesExist(Cinema cinema);
}
