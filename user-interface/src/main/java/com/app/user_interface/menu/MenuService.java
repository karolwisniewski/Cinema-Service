package com.app.user_interface.menu;

import com.app.persistence.dto.AuthenticationDto;
import com.app.persistence.dto.CreateFavouriteDto;
import com.app.persistence.dto.CreateMovieDto;
import com.app.persistence.dto.CreateUserDto;
import com.app.persistence.repositories.repository.criteria.MovieCriteria;
import com.app.persistence.views.TicketView;
import com.app.service.exceptions.MenuException;
import com.app.service.service.AuthenticationService;
import com.app.service.service.CinemaService;
import com.app.service.service.UsersService;
import com.app.user_interface.data.UserDataService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.app.user_interface.data.UserDataService.*;

@RequiredArgsConstructor

public class MenuService {

    private final CinemaService cinemaService;
    private final UsersService usersService;
    private final AuthenticationService authenticationService;

    private Integer userId;

    private int printMenu() {
        System.out.println("-------------------------- USER MANAGEMENT ---------------------");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Logout");

        System.out.println("-------------------------- USER ROLE ---------------------------");
        System.out.println("4. Buy ticket");
        System.out.println("5. Display all available movies");
        System.out.println("6. Display movies matching to criteria");
        System.out.println("7. Pay for reserved ticket");
        System.out.println("8. Generate history");

        System.out.println("-------------------------- ADMIN ROLE --------------------------");
        System.out.println("10. Add cinema rooms");
        System.out.println("11. Add seances");
        System.out.println("12. Send email message about promotions");
        System.out.println("13. Send email message to everyone");
        System.out.println("14. Generate and display statistics");

        System.out.println("0. Exit");
        return getInt("Choose option:");
    }

    public void showMainMenu() {
        do {
            int choice = printMenu();
            switch (choice) {
                case 0 -> {
                    System.out.println("Have a nice day");
                    return;
                }
                case 1 -> option1();
                case 2 -> option2();
                case 3 -> option3();
                case 4 -> option4();
                case 5 -> option5();
                case 6 -> option6();
                case 7 -> option7();
                case 8 -> option8();
                case 10 -> option10();
                case 11 -> option11();
                case 12 -> option12();
                case 13 -> option13();
                case 14 -> option14();
                default -> System.out.println("No option with number " + choice);
            }
        } while (true);
    }

    private void option1() {
        var createUserDto = CreateUserDto
                .builder()
                .name(UserDataService.getString("Enter your name:"))
                .surname(UserDataService.getString("Enter your surname:"))
                .username(UserDataService.getString("Enter your username:"))
                .email(UserDataService.getString("Enter your email:"))
                .emailConfirmation(UserDataService.getString("Confirm your email:"))
                .password(UserDataService.getString("Enter your password:"))
                .passwordConfirmation(UserDataService.getString("Confirm your password:"))
                .build();
        var registeredUsername = usersService.register(createUserDto);
        System.out.println("User with username " + registeredUsername + " registered!");
    }

    private void option2() {
        var authneticationData = AuthenticationDto
                .builder()
                .username(UserDataService.getString("Enter username:"))
                .password(UserDataService.getString("Enter password:"))
                .build();
        var loggedInUsername = authenticationService.login(authneticationData);

        userId = authenticationService.getAuthenticated().getId();
        System.out.println("Username " + loggedInUsername + " logged in!");
    }

    private void option3() {
        var loggedOutUsername = authenticationService.logout();
        System.out.println("Username " + loggedOutUsername + " logged out!");
    }

    private void option4() {
        authenticationService.userAccess();

        var buyTicketService = new BuyTicketService(cinemaService, userId);
        String message = buyTicketService.buyTicket();
        System.out.println(message);
        cinemaService.sendEmail(getEmail(), "Order confirmation", message);
    }

    private void option5() {
        authenticationService.userAccess();
        List<CreateMovieDto> allMovies = cinemaService.allMoviesInDataBase();
        addToFavourite(allMovies);
    }

    private void option6() {
        authenticationService.userAccess();
        MovieCriteria criteria = MovieCriteria
                .builder()
                .title(UserDataService.getStringNullAllowed("Insert title you are looking for: "))
                .category(UserDataService.getCategory("Insert category you are interesting in: "))
                .dateFrom(UserDataService.getLocalDateTime("Insert the date FROM which you want to see the movies."))
                .dateTo(UserDataService.getLocalDateTime("Insert the date TO which you want to see the movies."))
                .build();

        List<CreateMovieDto> moviesWithCriteria = cinemaService.filterMovies(criteria);
        addToFavourite(moviesWithCriteria);
    }


    private void option7() {
        authenticationService.userAccess();
        List<TicketView> reserved = cinemaService.findReservedTickets(userId);
        if (reserved.isEmpty()) {
            System.out.println("You have no reservations.");
            return;
        }

        String ticketsSting = reserved.stream()
                .map(ticket -> ticket.toString())
                .collect(Collectors.joining("\n"));

        String message = ticketsSting
                + "\nSum for reservations is: "
                + calculatePrice(reserved)
                + "\nDo you want to pay for your reservations?";

        if (UserDataService.yesOrNo(message)) {
            Integer updated = cinemaService.reservationToOrdered(userId);
            System.out.println("Success you pay for " + updated + " reservations!");
        }
    }


    private void option8() {
        authenticationService.userAccess();
        List<TicketView> views = cinemaService.getHistory(userId);
        Integer choice = getIntNullAllowed(
                "1. Send history in email " +
                        "\n2. Save history to file " +
                        "\nInsert your choice or press enter to continue");

        if (Objects.isNull(choice)) {
            return;
        } else if (choice.equals(1)) {
            String message = views
                    .stream()
                    .map(view -> view.toString())
                    .collect(Collectors.joining("\n"));
            String mail = cinemaService.getUserEmail(userId);

            cinemaService.sendEmail(mail, "History from cinema service", message);

        } else if (choice.equals(2)) {
            cinemaService.saveToFile("user_" + userId + "_history", views);
        } else {
            System.out.println("No option with number " + choice);
        }
    }

    private void option10() {
        authenticationService.adminAccess();
        String filename = getString("Insert filename with cinemas and rooms: ");
        System.out.println("List of id added cinemas records:");
        System.out.println(cinemaService.addCinemasAndRoomsFromFile(filename));
    }

    private void option11() {
        authenticationService.adminAccess();
        String filename = getString("Insert filename with movies and seances: ");
        System.out.println("List of id added seances records:");
        System.out.println(cinemaService.addMoviesAndSeances(filename));
    }


    private void option12() {
        authenticationService.adminAccess();
        Integer movieId = displayMoviesAndGetId();
        List<String> emails = cinemaService.getEmailsWithFavourites(movieId);
        String topic = getString("Insert message topic: ");
        String message = getString("Insert your message: ");

        emails.forEach(email -> cinemaService.sendEmail(email, topic, message));
    }

    private void option13() {
        authenticationService.adminAccess();
        List<String> emails = cinemaService.getAllEmails();
        String topic = getString("Insert message topic: ");
        String message = getString("Insert your message: ");

        emails.forEach(email -> cinemaService.sendEmail(email, topic, message));
    }

    private void option14(){
        authenticationService.adminAccess();
        System.out.println(cinemaService.generateStatistics());
    }

    // -----------------------------------
    // METODY POMOCNICZE
    // -----------------------------------

    private Integer displayMoviesAndGetId() {
        List<CreateMovieDto> moviesDto = cinemaService.allMoviesInDataBase();
        String moviesString = moviesDto
                .stream()
                .map(movie -> movie.getId() + ". " + movie.getTitle())
                .collect(Collectors.joining("\n"));

        Integer movieId = UserDataService.getInt(moviesString + "\nInsert movie id about which you want to sent message ");

        if (!moviesDto.stream().anyMatch(view -> view.getId().equals(movieId))) {
            throw new MenuException("Movie ID value is out of range");
        }
        return movieId;
    }

    private String getEmail() {
        return cinemaService.getUserEmail(userId);
    }

    private BigDecimal calculatePrice(List<TicketView> ticketViews) {
        return cinemaService.sumPrices(ticketViews);
    }

    private void addToFavourite(List<CreateMovieDto> moviesDto) {
        CreateFavouriteDto favouriteDto = CreateFavouriteDto
                .builder()
                .userId(userId)
                .movieId(UserDataService.getCreateMoviesIdentities(moviesDto, "Insert id you want to add to favourities"))
                .build();
        Long addedToDB = cinemaService.addToFavourite(favouriteDto);
        System.out.println("You add " + addedToDB + ". movies to favourites!");
    }
}
