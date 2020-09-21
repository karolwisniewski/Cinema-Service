package com.app.persistence.repositories.repository.impl;

import com.app.persistence.model.Ticket;
import com.app.persistence.model.enums.TicketTypeEnum;
import com.app.persistence.model.from_db.CityWithTicketType;
import com.app.persistence.repositories.repository.TicketRepository;
import com.app.persistence.repositories.repository.generic.AbstractCrudRepository;
import com.app.persistence.views.TicketView;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketRepositoryImpl extends AbstractCrudRepository<com.app.persistence.model.Ticket, Integer> implements TicketRepository {

    @Override
    public List<TicketView> generateTicketViewOfTicket(List<com.app.persistence.model.Ticket> ticketViews) {
        var GENERATE_TICKET_VIEW = """
                select  movies.title as movie_title, cinemas.name as cinema_name, rooms.name as room_name, seances.screening_date as screening_date, tickets.price, seats.roww as seat_row, seats.columnn as seat_column, tickets.ticket_type, tickets.status from tickets
                left join seances on tickets.seance_id = seances.id
                join movies on seances.movie_id = movies.id
                join rooms on seances.room_id = rooms.id
                join cinemas on cinemas.id = rooms.cinema_id
                join seats on seats.id = tickets.seat_id
                where (seances.id in(<seance_id>))
                and (seats.id in(<seat_id>));
                """;
        var seancesId = ticketViews
                .stream()
                .map(ticket -> ticket.getSeanceId().toString())
                .distinct()
                .collect(Collectors.toList());
        var seatsId = ticketViews
                .stream()
                .map(ticket -> ticket.getSeatId().toString())
                .distinct()
                .collect(Collectors.toList());

        return
                jdbi.withHandle(handle -> handle
                        .createQuery(GENERATE_TICKET_VIEW)
                        .bindList("seance_id", seancesId)
                        .bindList("seat_id", seatsId)
                        .mapToBean(TicketView.class)
                        .list());
    }

    @Override
    public Integer addAll(List<com.app.persistence.model.Ticket> ticketViews) {
        Handle handle = jdbi.open();
        PreparedBatch batch = handle
                .prepareBatch("INSERT INTO tickets (user_id, seance_id, seat_id, price, ticket_type, status) " +
                        "VALUES(:user_id, :seance_id, :seat_id, :price, :ticket_type, :status)");

        ticketViews.stream().forEach(ticket -> batch
                .bind("user_id", ticket.getUserId())
                .bind("seance_id", ticket.getSeanceId())
                .bind("seat_id", ticket.getSeatId())
                .bind("price", ticket.getPrice())
                .bind("ticket_type", ticket.getTicketType())
                .bind("status", ticket.getStatus())
                .add());

        return batch.execute().length;
    }

    @Override
    public List<Ticket> findReserved(Integer userId) {
        var FIND_RESERVED = """
                select * from tickets
                where (user_id = :user_id)
                and (status = 'RESERVATION');
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_RESERVED)
                .bind("user_id", userId)
                .mapToBean(Ticket.class)
                .list());
    }

    @Override
    public Integer updateMany(Integer userId) {
        var UPDATE_MANY = """
                update tickets set status = 'ORDERED'
                where user_id in (:userId);
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(UPDATE_MANY)
                .bind("userId", userId)
                .execute());

    }

    @Override
    public List<TicketView> findAllFroUser(Integer userId) {
        var GENERATE_TICKET_VIEW = """
                select  movies.title as movie_title, cinemas.name as cinema_name, rooms.name as room_name, seances.screening_date as screening_date, tickets.price, seats.roww as seat_row, seats.columnn as seat_column, tickets.ticket_type, tickets.status from tickets
                left join seances on tickets.seance_id = seances.id
                join movies on seances.movie_id = movies.id
                join rooms on seances.room_id = rooms.id
                join cinemas on cinemas.id = rooms.cinema_id
                join seats on seats.id = tickets.seat_id
                where tickets.user_id = :userId;
                """;

        return
                jdbi.withHandle(handle -> handle
                        .createQuery(GENERATE_TICKET_VIEW)
                        .bind("userId", userId)
                        .mapToBean(TicketView.class)
                        .list());
    }

    @Override
    public Optional<String> findCityWithTheGreatestAudience() {
        var FIND_CITY = """
                select city, max(ticket_counter) from cinemas_with_counted_movies
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_CITY)
                .map((rs, ctx) -> rs.getString("city"))
                .findFirst());
    }

    @Override
    public List<String[]> findMostPopularMovieInCity() {
        var FIND_MOVIE = """
                select * from cinemas_with_counted_movies;
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_MOVIE)
                .map((rs, ctx) -> (rs.getString("city") + ":" + rs.getString("title") + " -> " + rs.getInt("ticket_counter")).split(":"))
                .list());
    }

    @Override
    public List<String[]> findMostPopularCategoryInCity() {
        var FIND_CATEGORY = """
                select * from cinemas_with_counted_movies;
                """;

        return
                jdbi.withHandle(handle -> handle
                        .createQuery(FIND_CATEGORY)
                        .map((rs, ctx) -> (rs.getString("city") + ":" + rs.getString("category") + "->" + rs.getInt("ticket_counter")).split(":"))
                        .list());
    }

    @Override
    public List<CityWithTicketType> findMostPopularTicketTypeInCity() {
        var FIND_TICKET_TYPE = """
                select c.name, c.city, m.title, m.category,  t.ticket_type, count(*) as ticket_counter
                from movies m
                join seances s on m.id = s.movie_id\s
                join tickets t on t.seance_id = s.id
                join rooms r on s.room_id = r.id
                join cinemas c on r.cinema_id = c.id
                group by t.ticket_type, c.id;
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_TICKET_TYPE)
                .map((rs, ctx) -> CityWithTicketType.builder().city(rs.getString("city"))
                        .ticket_type(TicketTypeEnum.valueOf(rs.getString("ticket_type")))
                        .ticket_counter(rs.getInt("ticket_counter"))
                        .build())
                .list());

    }

    @Override
    public Map<String, BigDecimal> getTotalInComeInCity() {
        var FIND_TOTAL_INCOME = """
                select c.city,  count(*) as ticket_counter, sum(t.price) as price_sum
                from movies m
                join seances s on m.id = s.movie_id
                join tickets t on t.seance_id = s.id
                join rooms r on s.room_id = r.id
                join cinemas c on r.cinema_id = c.id
                group by c.city;
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_TOTAL_INCOME)
                .map((rs, ctx) -> (rs.getString("city") + "->" + rs.getString("price_sum")).split("->"))
                .stream()
                .collect(Collectors.toMap(
                        x -> x[0],
                        x -> new BigDecimal(x[1]),
                        (x1, x2) -> x1,
                        HashMap::new
                )));
    }

    @Override
    public Map<String, BigDecimal> getAverageTicketPriceInCity() {
        var FIND_AVERAGE_PRICe = """
                select c.city,  sum(t.price)/count(*) as avr
                from movies m
                join seances s on m.id = s.movie_id\s
                join tickets t on t.seance_id = s.id
                join rooms r on s.room_id = r.id
                join cinemas c on r.cinema_id = c.id
                group by c.city;
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_AVERAGE_PRICe)
                .map((rs, ctx) -> (rs.getString("city") + "->" + rs.getString("avr")).split("->"))
                .stream()
                .collect(Collectors.toMap(
                        x -> x[0],
                        x -> new BigDecimal(x[1]),
                        (x1, x2) -> x1,
                        HashMap::new
                )));
    }
}
