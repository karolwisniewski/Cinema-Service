package com.app.persistence.repositories.repository;

import com.app.persistence.model.TicketType;
import com.app.persistence.model.enums.TicketTypeEnum;
import com.app.persistence.repositories.repository.generic.CrudRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface TicketsTypeRepository extends CrudRepository<TicketType, Integer> {
    Optional<BigDecimal> getPriceForType(TicketTypeEnum type);

}
