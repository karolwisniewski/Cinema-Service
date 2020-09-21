package com.app.persistence.repositories.repository.impl;

import com.app.persistence.model.Seance;
import com.app.persistence.repositories.repository.SeanceRepository;
import com.app.persistence.repositories.repository.criteria.SeanceCriteria;
import com.app.persistence.repositories.repository.criteria.SearchCriteria;
import com.app.persistence.repositories.repository.generic.AbstractCrudRepository;
import com.app.persistence.views.SeanceView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SeanceRepositoryImpl extends AbstractCrudRepository<Seance, Integer> implements SeanceRepository {
    @Override
    public List<Seance> findByMovieNameAndCity(String city, String name) {
        var FIND_BY_MOVIE_NAME_SQL = """
                        select * from seances s 
                        join rooms r on s.room_id = r.id 
                        join cinemas c on r.cinema_id = c.id 
                        join movies m on m.id = s.movie_id
                        where c.city = :city and m.name = :name
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_BY_MOVIE_NAME_SQL))
                .bind("name", name)
                .bind("city", city)
                .mapToBean(Seance.class)
                .list();
    }

    @Override
    public List<SeanceView> findSeancesMatchingToCriteria(SeanceCriteria criteria) {
        var FIND_SEANACES_IN_CITY_ON_TIME = """
                select seances.id, seances.room_id, cinemas.city, cinemas.name, movies.title , movies.category, seances.screening_date 
                from seances 
                join rooms on seances.room_id = rooms.id
                join cinemas on rooms.cinema_id = cinemas.id
                join movies on seances.movie_id = movies.id
                where cinemas.city = :city
                and cinemas.name = :name 
                and screening_date between :from and :to
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_SEANACES_IN_CITY_ON_TIME)
                .bind("city", criteria.getCinema().getCity())
                .bind("name", criteria.getCinema().getName())
                .bind("from", criteria.getFrom())
                .bind("to", criteria.getTo())
                .mapToBean(SeanceView.class)
                .list());
    }

    @Override
    public List<SeanceView> findSeancesByCriteria(SearchCriteria searchCriteria) {
        List<String> queries = new ArrayList<>();

        var query = """
                select s.id, s.room_id, c.city, c.name, m.title, m.category, s.screening_date 
                from seances s 
                join rooms r on s.room_id = r.id
                join cinemas c on r.cinema_id = c.id
                join movies m on s.movie_id = m.id
                where ( """;

        for (String cr : searchCriteria.getCriteria()) {
            if (Objects.nonNull(cr) && !cr.isEmpty() && !cr.isBlank()) {
                if (cr.matches("\\d\\d:\\d\\d")) {
                    String[] dateArr = cr.split(":");
                    LocalDateTime date = LocalDateTime.now();
                    date = date.withHour(Integer.parseInt(dateArr[0]))
                            .withMinute(Integer.parseInt(dateArr[1]));

                    queries.add(" s.date >= '" + date.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm")) + "'");

                } else if (cr.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
                    String[] dateArr = cr.split("-");
                    LocalDateTime date = LocalDateTime.of(
                            Integer.parseInt(dateArr[0]),
                            Integer.parseInt(dateArr[1]),
                            Integer.parseInt(dateArr[2]),
                            00,
                            01);

                    queries.add(" s.date >= '" + date.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm")) + "'");

                } else {
                    queries.add(" c.name = " + "'" + cr + "'" + " or "
                            + " c.city = " + "'" + cr + "'" + " or "
                            + " m.title = " + "'" + cr + "'" + " or "
                            + " m.category = " + "'" + cr + "'");
                }
            }
        }
        var FIND_SEANCES_BY_CRITERIA = query + queries.stream().collect(Collectors.joining(" \n) and (")).concat(queries.size() > 1 ? ")" : ")");

        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_SEANCES_BY_CRITERIA)
                .mapToBean(SeanceView.class)
                .list());
    }

    @Override
    public Optional<Seance> doesExist(Seance seance) {
        var FIND_ROOM_IF_EXIST = """
                select * from seances 
                where movie_id = :movie_id
                and room_id = :room_id
                and screening_date = :screening_date;
                """;
        return jdbi.withHandle(handle ->
                handle.createQuery(FIND_ROOM_IF_EXIST)
                        .bind("movie_id", seance.getMovieId())
                        .bind("room_id", seance.getRoomId())
                        .bind("screening_date", seance.getScreeningDate())
                        .mapToBean(Seance.class)
                        .findFirst());
    }
}
