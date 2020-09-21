package com.app.user_interface.data;

import com.app.persistence.dto.CreateCinemaDto;
import com.app.persistence.dto.CreateMovieDto;
import com.app.persistence.model.enums.Category;
import com.app.persistence.model.enums.ReservationType;
import com.app.persistence.model.enums.TicketTypeEnum;
import com.app.persistence.repositories.repository.criteria.SearchCriteria;
import com.app.service.exceptions.UserDataServiceException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UserDataService {

    private static Scanner sc = new Scanner(System.in);

    public static String getString(String message){
        System.out.println(message);
        return sc.nextLine();
    }

    public static String getStringNullAllowed(String message){
        System.out.println(message);
        String text = sc.nextLine();
        if( text.isEmpty() || text.isBlank()){
            return null;
        }
        return text;
    }

    public static Integer getIntNullAllowed(String message){
        String value = getString(message);
        if(Objects.isNull(value) || value.isEmpty() || value.isBlank()){
            return null;
        }
        if(!value.matches("\\d+")){
            throw new UserDataServiceException("Value is not a number!");
        }
        return Integer.parseInt(value);
    }

    public static Integer getInt(String message){
        String value = getString(message);
        if(!value.matches("\\d+")){
            throw new UserDataServiceException("Value is not a number");
        }
        return Integer.parseInt(value);
    }

    public static Set<Integer> getCreateMoviesIdentities(List<CreateMovieDto> moviesDto, String message){
        if(Objects.isNull(moviesDto)){
            throw new UserDataServiceException("Movies DTO is null!");
        }
        String moviesDtoString = moviesDto
                .stream()
                .map(dto -> dto.toString())
                .collect(Collectors.joining("\n"));
        System.out.println(moviesDtoString);

        Integer id ;
        Set<Integer> identities = new TreeSet<>();
        Integer max = moviesDto
                .stream()
                .max(Comparator.comparing(CreateMovieDto::getId))
                .orElseThrow(() -> new UserDataServiceException("Movies comparing failed"))
                .getId();
        Integer min = moviesDto
                .stream()
                .min(Comparator.comparing(CreateMovieDto::getId))
                .orElseThrow(() -> new UserDataServiceException("Movies comparing failed"))
                .getId();

        do{
            id = getInt(message + " Or press \"ENTER\" to continue. ");

            if(Objects.nonNull(id)){
                if(id > max || id < min){
                throw new UserDataServiceException("Given ID is out of range!");
            }
                identities.add(id);
            }
        }while(!Objects.isNull(id));
        return identities;
    }

    public static LocalDate getLocalDate(String message){
        String dateString = getString(message + " \nInsert date in format: dd.mm.yyy");
        if(!dateString.matches("\\d{2}\\.\\d{2}\\.\\d{4}")){
            throw new UserDataServiceException("Incorrect date format");
        }
        String [] dateArr = dateString.split("\\.");
        LocalDate date;
        try {
            date = LocalDate.of(
                    Integer.parseInt(dateArr[2]),
                    Integer.parseInt(dateArr[1]),
                    Integer.parseInt(dateArr[0]));
        }catch (Exception e ){
            throw new UserDataServiceException(e.getMessage());
        }
        return date;
    }

    public static String getLocalDateTime(String message){
        String dateTimeString = getStringNullAllowed(message + " \nInsert time in format: yyyy.MM.dd HH:mm");
        if(Objects.isNull(dateTimeString) || dateTimeString.isEmpty() || dateTimeString.isBlank()){
            return null;
        }
        if(!dateTimeString.matches("\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}")){
            throw new UserDataServiceException("Incorrect dateTime format");
        }
        return dateTimeString + ":00";
    }

    public static String getCity(List<String> cities, String message){
        AtomicInteger counter = new AtomicInteger(1);
        String citiesMessage = cities
                .stream()
                .map(city -> counter.getAndIncrement() + ". " + city )
                .collect(Collectors.joining("\n"));

        int chosenCity = getInt(citiesMessage + "\n " +message);

        if(chosenCity < 0 || chosenCity > cities.size()){
            throw new UserDataServiceException("City number out of range!");
        }
        return cities.get(chosenCity-1);
    }

    public static SearchCriteria getCriteria(String message){
        String criteria = getString(message);
        String[] criteriaArr = criteria.split(" ");
        if(criteriaArr.length == 0){
            throw new UserDataServiceException("Incorrect criteria quantity");
        }

        return new SearchCriteria(Arrays.asList(criteriaArr));
    }

    public static CreateCinemaDto getCinema(List<CreateCinemaDto> cinemas, String message){
        AtomicInteger counter = new AtomicInteger(1);
        System.out.println(
        cinemas
                .stream()
                .map(city -> counter.getAndIncrement() + ". " + city )
                .collect(Collectors.joining("\n")));
        int chosenCinema = getInt(message);

        if(chosenCinema < 0 || chosenCinema > cinemas.size()){
            throw new UserDataServiceException("City number out of range!");
        }
        return cinemas.get(chosenCinema-1);
    }

    public static ReservationType getReservationType(String message){
        ReservationType[] types = ReservationType.values();

        AtomicInteger counter = new AtomicInteger(1);

        System.out.println(Arrays.stream(types)
                .map(type -> counter.getAndIncrement() + ". " + type.toString())
                .collect(Collectors.joining("\n")));
        int chosenType = getInt(message);
        if(chosenType > types.length){
            throw new UserDataServiceException("Incorrect value");
        }
        return types[chosenType-1];
    }

    public static Category getCategory(String message){
        AtomicInteger counter = new AtomicInteger(1);
        String messageToDisplay =
                Arrays.stream(Category.values())
                .map(category -> counter.getAndIncrement() + ". " + category.toString())
                .collect(Collectors.joining("\n"));

        Integer value = getIntNullAllowed(message +  " " + messageToDisplay);

        if(Objects.isNull(value)){
            return null;
        }

        if(value<1 || value > Category.values().length){
            throw new UserDataServiceException("No category with " + value + " value.");
        }
        return Category.values()[value-1];
    }

    public static TicketTypeEnum getTicketType(String message){
        AtomicInteger counter = new AtomicInteger(1);
        String messageToDisplay =
                Arrays.stream(TicketTypeEnum.values())
                        .map(category -> counter.getAndIncrement() + ". " + category.toString())
                        .collect(Collectors.joining("\n"));

        int value = getInt(message +  " \n" + messageToDisplay);

        if(value<1 || value > Category.values().length){
            throw new UserDataServiceException("No ticket type with " + value + " value.");
        }
        return TicketTypeEnum.values()[value-1];
    }

    public static boolean yesOrNo (String message){
        Integer choice = getInt(message + "\n1. YES\n2. NO");
        return switch (choice){
            case 1 -> true;
            case 2 -> false;
            default -> throw new UserDataServiceException("Incorrect value!");
        };
    }

}
