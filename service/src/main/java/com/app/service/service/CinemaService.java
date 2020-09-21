package com.app.service.service;

import com.app.persistence.converter.JsonCinemaRoomsConverter;
import com.app.persistence.converter.JsonSeanceMovieConverter;
import com.app.persistence.converter.JsonTicketViewConverter;
import com.app.persistence.dto.*;
import com.app.persistence.model.*;
import com.app.persistence.model.enums.Category;
import com.app.persistence.model.enums.TicketTypeEnum;
import com.app.persistence.model.from_db.CityWithTicketType;
import com.app.persistence.repositories.repository.*;
import com.app.persistence.repositories.repository.criteria.MovieCriteria;
import com.app.persistence.repositories.repository.criteria.SeanceCriteria;
import com.app.persistence.repositories.repository.criteria.SearchCriteria;
import com.app.persistence.views.SeanceView;
import com.app.persistence.views.SeatView;
import com.app.persistence.views.TicketView;
import com.app.service.exceptions.CinemaServiceException;
import com.app.service.mapper.Mappers;
import lombok.RequiredArgsConstructor;
import org.eclipse.collections.impl.collector.Collectors2;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CinemaService {

    private final MovieRepository movieRepository;
    private final FavouriteRepository favouriteRepository;
    private final SeanceRepository seanceRepository;
    private final CinemaRepository cinemaRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final EmailService emailService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final TicketsTypeRepository ticketsTypeRepository;

    // =================================================================
    //                    USER ROLE
    // =================================================================


    //Method returns list of all movies converted to DTO existing in database
    public List<CreateMovieDto> allMoviesInDataBase() {
        return movieRepository.findAll()
                .stream()
                .map(Mappers::movieToCreateMovieDto)
                .collect(Collectors.toList());
    }


    //Method returns list of movies converted to DTO matching to given criteria
    public List<CreateMovieDto> filterMovies(MovieCriteria criteria) {
        if (Objects.isNull(criteria)) {
            throw new CinemaServiceException("Element is null");
        }
        return movieRepository.findByMovieCriteria(criteria)
                .stream()
                .map(movie -> Mappers.movieToCreateMovieDto(movie))
                .collect(Collectors.toList());
    }


    // Method takes favourite DTO add new record to favourities
    public Long addToFavourite(CreateFavouriteDto favouriteDto) {
        if (Objects.isNull(favouriteDto)) {
            throw new CinemaServiceException("FavouriteDto object is null!");
        }
        return favouriteDto.getMovieId()
                .stream()
                .map(idx -> favouriteRepository.add(
                        Favourite.builder()
                                .movieId(idx)
                                .userId(favouriteDto.getUserId())
                                .build()))
                .collect(Collectors.counting());
    }

    // Method returns list of seances matching to given criteria
    public List<SeanceView> findSeancesByCriteria(SearchCriteria searchCriteria) {
        return seanceRepository.findSeancesByCriteria(searchCriteria);
    }


    // Method returns all Cities existing in database
    public List<String> getAllCities() {
        return cinemaRepository.findAllCities().stream().distinct().collect(Collectors.toList());
    }


    // Method returns list of cinemas in given city
    public List<CreateCinemaDto> getCinemasInCity(String city) {
        if (Objects.isNull(city)) {
            throw new CinemaServiceException("City name is null!");
        }
        return cinemaRepository.findCinemasInCity(city)
                .stream()
                .distinct()
                .map(Mappers::cinemaToCreateCinemaDto)
                .collect(Collectors.toList());
    }


    // Method returns list of seances matching to given criteria in readable form
    public List<SeanceView> getSeancesMatchingToCriteria(SeanceCriteria criteria) {
        return seanceRepository.findSeancesMatchingToCriteria(criteria);
    }


    // Method return list od seat from given seance id i readable form
    public List<SeatView> getPlaces(Integer seanceId) {
        if (Objects.isNull(seanceId)) {
            throw new CinemaServiceException("Seance id is null");
        }
        List<SeatView> busySeats = seatRepository.findBusySeats(seanceId);
        return seatRepository
                .findAllSeatsInSeance(seanceId)
                .stream()
                .map(seat -> Mappers.seatToSeatView(seat))
                .peek(view -> {
                    busySeats.forEach(busy -> {
                        if (busy.getId().equals(view.getId())) {
                            view.setStatus(busy.getStatus());
                        }
                    });
                })
                .collect(Collectors.toList());
    }


    // Method returns readable room plan for given seance id
    public String seatsToString(Integer seanceId, List<SeatView> seats) {
        Room room = roomRepository.findRoomBySeanceId(seanceId)
                .orElseThrow(() -> new CinemaServiceException("Room with given row place and room not exist in database!"));

        String[][] seatsArray = new String[room.getRowsNumber() + 1][room.getColumnsNumber() + 1];
        for (int i = 0; i < seatsArray.length; i++) {
            for (int j = 0; j < seatsArray[i].length; j++) {
                if (i == 0) {
                    seatsArray[i][j] = Integer.toString(j);
                } else {
                    seatsArray[i][j] = "F";
                    seatsArray[i][0] = Integer.toString(i);
                }
            }
        }
        seats.forEach(seat -> seatsArray[seat.getRoww()][seat.getColumnn()] = seat.getStatus().toString().substring(0, 1));

        return Arrays.stream(seatsArray).map(Arrays::toString).collect(Collectors.joining("\n"));
    }


    // Method check does given seats DTO are busy if not convert and change status
    public void takeSeats(CreateSeatDto createSeatDto) {
        String busyId = createSeatDto.getSeatsId().keySet().stream().filter(id -> seatRepository.isBusy(id, createSeatDto.getSeanceId())).map(seat -> seat.toString()).collect(Collectors.joining(", "));
        if (!busyId.isEmpty()) {
            throw new CinemaServiceException("Seats with id: " + busyId + " is busy!");
        }
    }


    //Method add list of tickets for seats given in DTO and return tickets list in readable form
    public List<TicketView> generateTicketsViewOfCreateSeatDto(CreateSeatDto seatDto) {
        if (Objects.isNull(seatDto)) {
            throw new CinemaServiceException("SeatDto is null!");
        }
        List<Ticket> tickets = Mappers.createSeatDtoToTickets(seatDto);
        tickets.forEach(ticket -> ticket.setPrice(getPriceForTicketType(ticket.getTicketType())));
        ticketRepository.addAll(tickets);

        return ticketRepository.generateTicketViewOfTicket(tickets);
    }

    private BigDecimal getPriceForTicketType(TicketTypeEnum type){
        return ticketsTypeRepository.getPriceForType(type).orElseThrow(() -> new CinemaServiceException("Getting price failed"));
    }

    // Method is responsible for seanding emails to users
    public void sendEmail(String address, String title, String message) {
        emailService.sendAsHtml(address, title, "<h1>" + message + "</h1>");
    }


    //Method returns only resered tickets for given user ID
    public List<TicketView> findReservedTickets(Integer userId) {
        List<Ticket> tickets = ticketRepository.findReserved(userId);
        return ticketRepository.generateTicketViewOfTicket(tickets);
    }

    //Method returns sum of prices of given tickets views
    public BigDecimal sumPrices(List<TicketView> ticketViews) {
        return ticketViews
                .stream()
                .map(ticket -> ticket.getPrice())
                .collect(Collectors2.summingBigDecimal(price -> price));
    }


    //Method change status RESERVATION to ORDERED in all records belonging to given user ID
    public Integer reservationToOrdered(Integer userId) {
        return ticketRepository.updateMany(userId);
    }


    //Method returns all tickets id DB belonging to given user ID
    public List<TicketView> getHistory(Integer userId) {
        return ticketRepository.findAllFroUser(userId);
    }


    //Method returns mail adress belonging to given user ID
    public String getUserEmail(Integer userId) {
        return userRepository.getEmail(userId).orElseThrow(() -> new CinemaServiceException("Getting mail from Database failed"));
    }


    //Method save to file with given name given string
    public void saveToFile(String filename, List<TicketView> views) {
        new JsonTicketViewConverter(filename)
                .toJson(views);
    }

    // =================================================================
    //                    ADMIN ROLE
    // =================================================================

    public Integer addMovieToDB(CreateMovieDto movieDto) {
        return movieRepository
                .add(Mappers.createMovieDtoToMovie(movieDto))
                .orElseThrow(() -> new CinemaServiceException("Adding to Data Base failed"))
                .getId();
    }


    public Integer updateMovieRecord(Movie movie) {
        return movieRepository
                .update(movie)
                .orElseThrow(() -> new CinemaServiceException("Updating record in Data Base failed"))
                .getId();
    }


    //Method returns list of email addresses of users who have movie with given id in favourites
    public List<String> getEmailsWithFavourites(Integer movieId) {
        return userRepository.getEmailList(movieId);
    }


    //Method returns all email addresses existing in database
    public List<String> getAllEmails() {
        return userRepository.getAllEmails();
    }


    public String generateStatistics() {
       return Statistic
                .builder()
                .cityWithGreatestAudience(determineCityWithTheGreatestAudience())
                .mostPopularMovieInCity(determineMostPopularMovieInCity())
                .mostPopularCategoryInCity(determinateMostPopularCategoryInCity())
                .mostPopularTicketTypeInCity(determinateMostPopularTicketTypeInCity())
                .averageTicketPriceInCity(determinateAverageTicketPriceInCity())
                .totalIncomeInCity(determinateTotalIncomeInCity())
                .build()
                .toString();
    }


    private Map<String, BigDecimal> determinateTotalIncomeInCity() {
        return ticketRepository.getTotalInComeInCity();
    }


    private Map<String, TicketTypeEnum> determinateMostPopularTicketTypeInCity() {
        return ticketRepository
                .findMostPopularTicketTypeInCity()
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(CityWithTicketType::getCity),
                        cwt -> cwt
                                .entrySet()
                                .stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        r -> r.getValue()
                                                .stream()
                                                .max(Comparator.comparing(CityWithTicketType::getTicket_counter))
                                                .orElseThrow(() -> new CinemaServiceException("Can not compare list of CityWithTicketType objects"))
                                                .getTicket_type(),
                                        (r1, r2) -> r1,
                                        HashMap::new
                                ))));
    }


    private Map<String, BigDecimal> determinateAverageTicketPriceInCity() {
        return ticketRepository.getAverageTicketPriceInCity();
    }


    private Map<String, Category> determinateMostPopularCategoryInCity() {
        List<String[]> records = ticketRepository.findMostPopularCategoryInCity();

        return records
                .stream()
                .collect(Collectors.toMap(
                        row -> row[0],
                        row -> Category.valueOf(records
                                .stream()
                                .filter(c -> c[0] == row[0])
                                .map(c -> c[1].split("->"))
                                .collect(Collectors.toMap(
                                        c -> c[0],
                                        c -> c[1],
                                        (c1, c2) -> c1 + c2,
                                        HashMap::new
                                ))
                                .entrySet()
                                .stream()
                                .max(Comparator.comparing(c -> c.getValue()))
                                .orElseThrow()
                                .getKey()),
                        (r1, r2) -> r1,
                        HashMap::new
                ));
    }

    private String determineCityWithTheGreatestAudience() {
        return ticketRepository.findCityWithTheGreatestAudience()
                .orElseThrow(() -> new CinemaServiceException("Getting city from database failed"));
    }


    private Map<String, String> determineMostPopularMovieInCity() {
        return ticketRepository.findMostPopularMovieInCity()
                .stream()
                .collect(Collectors.toMap(
                        row -> row[0],
                        row -> row[1],
                        (r1, r2) -> r1.charAt(r1.length() - 1) > r2.charAt(r2.length() - 1) ? r1 : r2,
                        HashMap::new
                ));
    }

    // ------------------------------------------------------------------------------------------------------------------
    // USTALANIE STRUKTURY KIN, SAL KINOWYCH I SIEDZEN
    // ------------------------------------------------------------------------------------------------------------------


    //Method add cinemas and rooms to database from given JSON file
    public List<Integer> addCinemasAndRoomsFromFile(String filename) {
        return new JsonCinemaRoomsConverter(filename)
                .fromJson()
                .orElseThrow(() -> new CinemaServiceException("From Json converting failed"))
                .stream()
                .map(createCinemaAndRoomsDto -> {
                    var cinemaId = updateCinema(createCinemaAndRoomsDto);
                    var roomsId = updateRooms(createCinemaAndRoomsDto.getRooms(), cinemaId);
                    return cinemaId;
                })
                .collect(Collectors.toList());
    }

    //Method add seances and movies from given JSON file
    public List<Integer> addMoviesAndSeances(String filename) {
        return new JsonSeanceMovieConverter(filename)
                .fromJson()
                .orElseThrow(() -> new CinemaServiceException("From Json converting failed"))
                .stream()
                .map(seanceDto -> {
                    Integer movieId = updateMovie(seanceDto.getMovieDto());
                    return updateSeance(seanceDto, movieId);
                })
                .collect(Collectors.toList());
    }


    private Integer updateCinema(CreateCinemaAndRoomsDto createCinemaAndRoomsDto) {
        var cinema = Mappers.createCinemaAndRoomsDtoToCinema(createCinemaAndRoomsDto);

        return cinemaRepository
                .doesExist(cinema)
                .orElseGet(() -> cinemaRepository
                        .add(cinema)
                        .orElseThrow(() -> new CinemaServiceException("Cannot add new cinema")))
                .getId();
    }

    private List<Integer> updateRooms(List<CreateRoomDto> rooms, Integer cinemaId) {
        return rooms
                .stream()
                .map(room -> updateRoom(room, cinemaId))
                .collect(Collectors.toList());
    }

    private Integer updateRoom(CreateRoomDto createRoomDto, Integer cinemaId) {
        var room = Mappers.createRoomDtoToRoom(createRoomDto, cinemaId);

        return roomRepository
                .doesExist(room)
                .map(roomFromDb -> {
                    room.setId(roomFromDb.getId());
                    var updatedRoom = roomRepository
                            .update(room)
                            .orElseThrow(() -> new CinemaServiceException("....."));
                    deleteSeats(updatedRoom.getId());
                    generateSeats(updatedRoom);
                    return updatedRoom;
                }).orElseGet(() -> {
                    var addedRoom = roomRepository
                            .add(room)
                            .orElseThrow(() -> new CinemaServiceException("Cannot add new room"));
                    generateSeats(addedRoom);
                    return addedRoom;
                }).getId();
    }

    private int generateSeats(Room room) {
        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= room.getRowsNumber(); i++) {
            for (int j = 1; j <= room.getColumnsNumber(); j++) {
                seats.add(Seat
                        .builder()
                        .roww(i)
                        .columnn(j)
                        .roomId(room.getId())
                        .build());
            }
        }
        return seatRepository.addAllSeatsInRoom(seats);
    }


    private void deleteSeats(Integer roomId) {
        seatRepository.deleteAllFromRoom(roomId);
    }

    private Integer updateMovie(CreateMovieDto movieDto) {
        Movie movie = Mappers.createMovieDtoToMovie(movieDto);

        return movieRepository
                .doesExist(movie)
                .flatMap(m -> movieRepository.update(movie))
                .orElseGet(() -> movieRepository
                        .add(movie)
                        .orElseThrow(() -> new CinemaServiceException("Cannot add new Movie")))
                .getId();
    }

    private Integer updateSeance(CreateMovieAndSeanceDto seanceDto, Integer movieId) {
        Seance seance = Mappers.createSeanceAndMovieToSeance(seanceDto, movieId);

        return seanceRepository
                .doesExist(seance)
                .orElseGet(() -> {
                    Seance newSeance = seanceRepository
                            .add(seance)
                            .orElseThrow(() -> new CinemaServiceException("Cannot add new Seance"));
                    return newSeance;
                })
                .getId();
    }
}
