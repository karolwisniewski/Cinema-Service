package com.app.persistence.model;

import com.app.persistence.model.enums.Status;
import com.app.persistence.model.enums.TicketTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@lombok.Data
@NoArgsConstructor
@Builder
@AllArgsConstructor

public class Ticket {

    private Integer id;
    private Integer userId;
    private Integer seanceId;
    private Integer seatId;
    private TicketTypeEnum ticketType;
    private Status status;
    private  BigDecimal price;

}
