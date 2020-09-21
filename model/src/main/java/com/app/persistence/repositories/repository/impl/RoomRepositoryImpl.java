package com.app.persistence.repositories.repository.impl;

import com.app.persistence.model.Room;
import com.app.persistence.repositories.repository.RoomRepository;
import com.app.persistence.repositories.repository.generic.AbstractCrudRepository;

import java.util.Optional;

public class RoomRepositoryImpl extends AbstractCrudRepository<Room, Integer> implements RoomRepository {
    @Override
    public Optional<Room> findRoomBySeanceId(Integer seanceId) {
        var FIND_ROOM_BY_SEANCE_ID = """
                select * from rooms r
                where r.id = (select room_id from seances where id = :seance_id);              
                  """;
        return jdbi.withHandle(handle ->
                handle.createQuery(FIND_ROOM_BY_SEANCE_ID)
                .bind("seance_id", seanceId)
                .mapToBean(Room.class)
                .findFirst());
    }

    @Override
    public Optional<Room> doesExist(Room room) {
        var FIND_ROOM_IF_EXIST = """
                select * from rooms 
                where cinema_id = :cinema_id
                and name = :name;
                """;
        return jdbi.withHandle(handle ->
                handle.createQuery(FIND_ROOM_IF_EXIST)
                .bind("cinema_id", room.getCinemaId())
                .bind("name", room.getName())
                .mapToBean(Room.class)
                .findFirst());
    }
}
