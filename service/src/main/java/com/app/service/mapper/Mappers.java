package com.app.service.mapper;

import com.app.persistence.model.enums.Role;
import com.app.persistence.dto.*;
import com.app.persistence.model.*;
import com.app.persistence.model.enums.ReservationType;
import com.app.persistence.model.enums.Status;
import com.app.persistence.views.SeatView;

import java.util.List;
import java.util.stream.Collectors;

public interface Mappers {
     static CreateMovieDto movieToCreateMovieDto(Movie movie) {
        return CreateMovieDto
                .builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .category(movie.getCategory())
                .displaySince(movie.getDisplaySince())
                .displayTo(movie.getDisplayTo())
                .build();
    }

    static Movie createMovieDtoToMovie (CreateMovieDto movieDto) {
        return Movie
                .builder()
                .title(movieDto.getTitle())
                .category(movieDto.getCategory())
                .displaySince(movieDto.getDisplaySince())
                .displayTo(movieDto.getDisplayTo())
                .build();
    }

    static SeatView seatToSeatView(Seat seat){
         return SeatView
                 .builder()
                 .id(seat.getId())
                 .roww(seat.getRoww())
                 .columnn(seat.getColumnn())
                 .status(Status.FREE)
                 .build();
    }

    static CreateCinemaDto cinemaToCreateCinemaDto(Cinema cinema){
         return CreateCinemaDto
                 .builder()
                 .name(cinema.getName())
                 .city(cinema.getCity())
                 .build();
    }

    static Seance createSeanceAndMovieToSeance(CreateMovieAndSeanceDto seanceDto, Integer movieId){
        return Seance
                 .builder()
                 .movieId(movieId)
                 .roomId(seanceDto.getRoomId())
                 .screeningDate(seanceDto.getScreeningDate())
                 .build();
    }

    static Cinema createCinemaAndRoomsDtoToCinema(CreateCinemaAndRoomsDto createCinemaAndRoomsDtoDto){
         return Cinema
                 .builder()
                 .name(createCinemaAndRoomsDtoDto.getCinema().getName())
                 .city(createCinemaAndRoomsDtoDto.getCinema().getCity())
                 .build();
    }

    static Room createRoomDtoToRoom(CreateRoomDto roomDto, Integer cinemaId){
         return Room
                 .builder()
                 .name(roomDto.getName())
                 .columnsNumber(roomDto.getColumnsNumber())
                 .rowsNumber(roomDto.getRowsNumber())
                 .cinemaId(cinemaId)
                 .build();
    }

    static List<Ticket> createSeatDtoToTickets(CreateSeatDto seatDto){
       return seatDto
                .getSeatsId()
                .keySet()
                .stream()
                .map(id -> Ticket
                        .builder()
                        .userId(seatDto.getUserId())
                        .seanceId(seatDto.getSeanceId())
                        .seatId(id)
                        .ticketType(seatDto.getSeatsId().get(id))
                        .status(seatDto.getReservationType().equals(ReservationType.BUY) ? Status.ORDERED : Status.RESERVATION)
                        .build())
                .collect(Collectors.toList());
    }

    static Cinema createCinemaDtoToCinema(CreateCinemaDto cinemaDto){
         return Cinema
                 .builder()
                 .name(cinemaDto.getName())
                 .city(cinemaDto.getCity())
                 .build();
    }

    static User fromCreateUserDtoToUser(CreateUserDto createUserDto) {
         return User
                 .builder()
                 .name(createUserDto.getName())
                 .surname(createUserDto.getSurname())
                 .username(createUserDto.getUsername())
                 .userRole(Role.USER)
                 .email(createUserDto.getEmail())
                 .password(createUserDto.getPassword())
                 .build();
    }
}
