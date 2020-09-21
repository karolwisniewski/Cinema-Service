package com.app.persistence.repositories.repository;

import com.app.persistence.model.Seat;
import com.app.persistence.repositories.repository.generic.CrudRepository;
import com.app.persistence.views.SeatView;

import java.util.List;

public interface SeatRepository extends CrudRepository<Seat, Integer> {
    List<SeatView> findBusySeats(Integer seanceId);
    List<Seat> findAllSeatsInSeance(Integer seanceId);
    boolean isBusy(Integer id, Integer seanceId);
    void deleteAllFromRoom(Integer roomId);
    Integer addAllSeatsInRoom(List<Seat> seats);

}
