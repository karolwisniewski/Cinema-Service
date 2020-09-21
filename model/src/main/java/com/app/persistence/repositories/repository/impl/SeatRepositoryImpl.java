package com.app.persistence.repositories.repository.impl;

import com.app.persistence.model.Seat;
import com.app.persistence.repositories.repository.SeatRepository;
import com.app.persistence.repositories.repository.generic.AbstractCrudRepository;
import com.app.persistence.views.SeatView;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.List;

public class SeatRepositoryImpl extends AbstractCrudRepository<Seat, Integer> implements SeatRepository {

    @Override
    public boolean isBusy(Integer id, Integer seanceId) {
        var FIND_STATUS_BY_ID = """
                select id from tickets 
                where (seat_id = :seat_id)
                and (seance_id = :seanceId);
                """;

        return jdbi.withHandle(handle ->
                handle.createQuery(FIND_STATUS_BY_ID)
                        .bind("seat_id", id)
                        .bind("seanceId", seanceId)
                        .map((rs, ctx) -> rs.getInt("id"))
                        .findFirst()
                        .isPresent());

    }

    @Override
    public void deleteAllFromRoom(Integer roomId) {
        var DELETE_ALL_FROM_ROOM = """
                delete from seats where room_id = :room_id;
                """;

        jdbi.useHandle(handle -> handle
                .createUpdate(DELETE_ALL_FROM_ROOM)
                .bind("room_id", roomId)
                .execute());
    }

    @Override
    public Integer addAllSeatsInRoom(List<Seat> seats) {
        Handle handle = jdbi.open();
        PreparedBatch batch = handle
                .prepareBatch("INSERT INTO seats(room_id, roww, columnn) VALUES(:room_id, :roww, :columnn)");

        seats.stream().forEach(seat -> batch
                .bind("room_id", seat.getRoomId())
                .bind("roww", seat.getRoww())
                .bind("columnn", seat.getColumnn())
                .add());

        return batch.execute().length;
    }

    @Override
    public List<SeatView> findBusySeats(Integer seanceId) {
        var FIND_SEATS = """
                select s.id, t.status, s.roww, s.columnn from seats s
                join tickets t on t.seat_id = s.id
                where t.seance_id = :seance_id;
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_SEATS)
                .bind("seance_id", seanceId)
                .mapToBean(SeatView.class)
                .list());
    }

    @Override
    public List<Seat> findAllSeatsInSeance(Integer seanceId) {
        var FIND_SEATS = """
                select s.id, s.room_id, s.roww, s.columnn from seats s
                join rooms r on r.id = s.room_id
                join seances se on se.room_id = r.id
                where se.id = :seance_id;
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_SEATS)
                .bind("seance_id", seanceId)
                .mapToBean(Seat.class)
                .list());
    }
}
