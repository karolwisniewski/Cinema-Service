package com.app.user_interface.menu;

import com.app.persistence.dto.CreateCinemaDto;
import com.app.persistence.dto.CreateSeatDto;
import com.app.persistence.model.enums.ReservationType;
import com.app.persistence.repositories.repository.criteria.SeanceCriteria;
import com.app.persistence.repositories.repository.criteria.SearchCriteria;
import com.app.persistence.views.SeanceView;
import com.app.persistence.views.SeatView;
import com.app.persistence.views.TicketView;
import com.app.user_interface.data.UserDataService;
import com.app.service.service.CinemaService;
import com.app.service.exceptions.MenuException;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.app.user_interface.data.UserDataService.getInt;
import static com.app.user_interface.data.UserDataService.getReservationType;

@RequiredArgsConstructor

public class BuyTicketService {
    private final CinemaService cinemaService;
    private final Integer userId;


    public String buyTicket() {
        String message = "1. Find seance by criteria.\n2. Find seance step by step. \nInsert your choice: ";
        int choice = UserDataService.getInt(message);

        return
        switch (choice) {
            case 1 -> byCriteriaProcedure();
            case 2 -> stepByStepProcedure();
            default ->
                "No option with number " + choice;
        };
    }

    private String stepByStepProcedure() {
        List<SeanceView> seanceViews = selectSeancesViewForProcedure2();
        return beginReservation(seanceViews);
    }

    private List<SeanceView> selectSeancesViewForProcedure2() {
        List<String> cities = cinemaService.getAllCities();
        String chosenCity = UserDataService.getCity(cities, "Insert the city you are looking for a cinemas: ");
        List<CreateCinemaDto> cinemasDto = cinemaService.getCinemasInCity(chosenCity);
        CreateCinemaDto cinemaDto = UserDataService.getCinema(cinemasDto, "Insert the cinema you want: ");
        SeanceCriteria criteria = SeanceCriteria
                .builder()
                .cinema(cinemaDto)
                .from(UserDataService.getLocalDateTime("Insert the date FROM which you want to see the seances. "))
                .to(UserDataService.getLocalDateTime("Insert the date TO which you want to see the seances. "))
                .build();

        return cinemaService.getSeancesMatchingToCriteria(criteria);
    }


    private String byCriteriaProcedure() {
        List<SeanceView> seanceViews = findSeances("Insert searching criteria");
        if(seanceViews.isEmpty()){
            throw new MenuException("No seances with given criteria");
        }
        return beginReservation(seanceViews);
    }


    private String beginReservation(List<SeanceView> seances) {
        if (seances.isEmpty()) {
            throw new MenuException("No seances with given criteria!");
        }
        if (Objects.isNull(seances)) {
            throw new MenuException("List of SeanceView is null!");
        }

        Integer seanceId = displaySeancesAndGetId(seances);
        List<SeatView> seats = displaySeatsAndReturnListOfThem(seances, seanceId);
        List<SeatView> seatsId = getNumberOdTicketsAndLocalization(seats);
        CreateSeatDto seatsDto = takeSeats(seanceId, seatsId);

        String message = generateTickets(seatsDto)
                .stream()
                .map(com.app.persistence.views.TicketView::toString)
                .collect(Collectors.joining("\n"));

        if (seatsDto.getReservationType().equals(ReservationType.BUY)) {
            return "Congratulations you bought " + seatsDto.getSeatsId().size() + " tickets.\n" +  message; }
        else{
            return "Congratulations you reserved " + seatsDto.getSeatsId().size() + " seats.\n" + message; }
    }


    private List<SeanceView> findSeances(String message) {
        SearchCriteria criteria = UserDataService.getCriteria(message);
        return cinemaService.findSeancesByCriteria(criteria);
    }


    private Integer displaySeancesAndGetId(List<SeanceView> seances) {
        if(Objects.isNull(seances)){
            throw new MenuException("List of seances is null!");
        }
        System.out.println(seances
                .stream()
                .map(SeanceView::toString)
                .collect(Collectors.joining("\n")));

        return getInt("Insert seance ID: ");
    }


    private List<SeatView> displaySeatsAndReturnListOfThem(List<SeanceView> seances, Integer seanceId) {

        if (seanceId <= 0 || seanceId > seances.stream()
                .max(Comparator.comparing(SeanceView::getId))
                .orElseThrow(() -> new MenuException("Comparing failed")).getId()) {
            throw new MenuException("Given seance ID is out of range!");
        }

        List<SeatView> seats = cinemaService.getPlaces(seanceId);
        System.out.println(cinemaService.seatsToString(seanceId, seats));

        return seats;
    }


    private List<SeatView> getNumberOdTicketsAndLocalization(List<SeatView> seats) {
        if(Objects.isNull(seats)){
            throw new MenuException("List of seats is null!");
        }
        int quantity = UserDataService.getInt("How many tickets?");

        List<SeatView> seatsId = IntStream.range(0, quantity).boxed().map(index -> {
            final int row = getInt("Insert row number for " + (index + 1) + ". ticket: ");
            final int column = getInt("Insert column number for " + (index + 1) + ". ticket: ");

            return seats.stream()
                    .filter(seat -> seat.getRoww() == row && seat.getColumnn() == column)
                    .findFirst()
                    .orElseThrow(() -> new MenuException("Incorrect row or column number")); })
                .distinct()
                .collect(Collectors.toList());

        if (quantity != seatsId.size()) {
            throw new MenuException("You have chosen the same place many times!");
        }
        return seatsId;
    }

    private CreateSeatDto takeSeats(Integer seanceId, List<SeatView> seatsId) {
        if(Objects.isNull(seanceId)){
            throw new MenuException("Seance id is null!");
        }
        CreateSeatDto seatDto = CreateSeatDto
                .builder()
                .userId(userId)
                .seanceId(seanceId)
                .seatsId(seatsId
                        .stream()
                        .map(SeatView::getId)
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> UserDataService.getTicketType("Insert ticket type for seat with"
                                        + seatsId.stream()
                                        .filter(seat -> seat.getId().equals(id))
                                        .map(seat -> "row = " + seat.getRoww() + " and " + "column = " + seat.getColumnn())
                                        .collect(Collectors.joining(""))),
                                (id1, id2) -> id1,
                                HashMap::new)))
                .reservationType(getReservationType("Chose reservation type: "))
                .build();
        cinemaService.takeSeats(seatDto);
        return seatDto;
    }

    private List<TicketView> generateTickets(CreateSeatDto seatDto) {
        if(Objects.isNull(seatDto)){
            throw new MenuException("Seat DTO is null!");
        }
        return cinemaService.generateTicketsViewOfCreateSeatDto(seatDto);
    }
}
