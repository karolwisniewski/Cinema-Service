package com.app.persistence.dto;

import com.app.persistence.model.enums.ReservationType;
import com.app.persistence.model.enums.TicketTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateSeatDto {

    Map<Integer, TicketTypeEnum> seatsId;
    private Integer seanceId;
    private ReservationType reservationType;
    private Integer userId;

}
