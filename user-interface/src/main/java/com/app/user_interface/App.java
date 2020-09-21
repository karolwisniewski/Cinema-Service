package com.app.user_interface;

import com.app.persistence.repositories.repository.impl.*;
import com.app.service.service.*;
import com.app.user_interface.menu.MenuService;

public class App {
    public static void main(String[] args) {

        var movieRepository = new MovieRepositoryImpl();
        var favouriteRepository = new FavouriteRepositoryImpl();
        var seanceRepository = new SeanceRepositoryImpl();
        var cinemaRepository = new CinemaRepositoryImpl();
        var seatRepository = new SeatRepositoryImpl();
        var ticketRepository = new TicketRepositoryImpl();
        var roomRepository = new RoomRepositoryImpl();
        var userRepository = new UserRepositoryImpl();
        var ticketTypeRepository = new TicketsTypeRepositoryImpl();

        var emailService = new EmailService();
        var userService = new UsersService(userRepository);
        var authenticationService = new AuthenticationService(userRepository);

        CinemaService cinemaService;
        cinemaService = new CinemaService(
                movieRepository,
                favouriteRepository,
                seanceRepository,
                cinemaRepository,
                seatRepository,
                ticketRepository,
                emailService,
                roomRepository,
                userRepository,
                ticketTypeRepository
                );

        MenuService menuService = new MenuService(cinemaService, userService, authenticationService);
        menuService.showMainMenu();

        cinemaService.generateStatistics();



        //MoviesAndSeances.json
        //CinemaAndRooms.json
    }
}
