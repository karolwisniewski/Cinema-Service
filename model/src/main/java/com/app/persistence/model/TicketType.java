package com.app.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class TicketType {
    private Integer id;
    private TicketType name;
    private BigDecimal basePrice;
    private BigDecimal discount;
}