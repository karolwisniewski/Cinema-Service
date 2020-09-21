package com.app.persistence.repositories.repository.impl;

import com.app.persistence.model.Cinema;
import com.app.persistence.repositories.repository.CinemaRepository;
import com.app.persistence.repositories.repository.generic.AbstractCrudRepository;

import java.util.List;
import java.util.Optional;

public class CinemaRepositoryImpl extends AbstractCrudRepository<Cinema, Integer> implements CinemaRepository {

    @Override
    public List<String> findAllCities() {
        var FIND_ALL_CITIES = """
                select city from cinemas;
                """ ;
        return jdbi.withHandle(handle -> handle
                    .createQuery(FIND_ALL_CITIES)
                    .map((rs, ctx) -> rs.getString("city"))
                    .list());
    }

    @Override
    public List<Cinema> findCinemasInCity(String city) {
        var FIND_CINEMAS_IN_CITY = """
                select * from cinemas where city = :city;
                """;
        return  jdbi.withHandle(handle -> handle
                    .createQuery(FIND_CINEMAS_IN_CITY)
                    .bind("city", city)
                    .mapToBean(Cinema.class)
                    .list());
    }

    @Override
    public Optional<Cinema> doesExist(Cinema cinema) {
        var FIND_CINEMA_IF_EXIST = """
                select * from cinemas 
                where name = :name
                and city = :city ;
                """;
        return jdbi.withHandle(handle ->
                handle.createQuery(FIND_CINEMA_IF_EXIST)
                .bind("name", cinema.getName())
                .bind("city", cinema.getCity())
                .mapToBean(Cinema.class)
                .findFirst());
    }
}
