package com.app.persistence.repositories.repository;

import com.app.persistence.model.Ticket;
import com.app.persistence.model.from_db.CityWithTicketType;
import com.app.persistence.repositories.repository.generic.CrudRepository;
import com.app.persistence.views.TicketView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TicketRepository extends CrudRepository<com.app.persistence.model.Ticket, Integer> {
    List<TicketView> generateTicketViewOfTicket(List<com.app.persistence.model.Ticket> tickets);
    Integer addAll (List<com.app.persistence.model.Ticket> tickets);
    List<Ticket> findReserved(Integer userId);
    Integer updateMany(Integer userId);
    List<TicketView> findAllFroUser(Integer userId);
    Optional<String> findCityWithTheGreatestAudience();
    List<String[]> findMostPopularMovieInCity();
    List<String[]> findMostPopularCategoryInCity();
    List<CityWithTicketType> findMostPopularTicketTypeInCity();
    Map<String, BigDecimal> getTotalInComeInCity();
    Map<String, BigDecimal> getAverageTicketPriceInCity();
}
