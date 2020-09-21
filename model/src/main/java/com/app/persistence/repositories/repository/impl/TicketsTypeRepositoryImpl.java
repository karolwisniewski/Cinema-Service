package com.app.persistence.repositories.repository.impl;

import com.app.persistence.model.TicketType;
import com.app.persistence.model.enums.TicketTypeEnum;
import com.app.persistence.repositories.repository.TicketsTypeRepository;
import com.app.persistence.repositories.repository.generic.AbstractCrudRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class TicketsTypeRepositoryImpl extends AbstractCrudRepository<TicketType, Integer> implements TicketsTypeRepository {

    @Override
    public Optional<BigDecimal> getPriceForType(TicketTypeEnum type) {
        var FIND_PRICE = """
                select base_price-(base_price*discount) as price from tickets_type where name = :name;
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(FIND_PRICE)
                .bind("name", type)
                .map((rs, ctx) -> rs.getBigDecimal("price"))
                .findFirst());
    }
}
