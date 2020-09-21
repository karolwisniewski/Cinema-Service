package com.app.persistence.repositories.repository;

import com.app.persistence.model.Room;
import com.app.persistence.repositories.repository.generic.CrudRepository;

import java.util.Optional;

public interface RoomRepository extends CrudRepository<Room, Integer> {
    Optional<Room> findRoomBySeanceId(Integer seanceId);
    Optional<Room> doesExist(Room room);
}
