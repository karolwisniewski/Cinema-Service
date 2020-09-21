package com.app.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateTicketDto {

    private Integer seanceId;
    private Integer userId;
    private Integer seatId;
    private BigDecimal price;
    private BigDecimal discount;

    @Override
    public String toString() {
        return "CreateTicketDto{" +
                "seanceId=" + seanceId +
                ", userId=" + userId +
                ", seatId=" + seatId +
                ", price=" + price +
                ", discount=" + discount +
                '}';
    }
}
