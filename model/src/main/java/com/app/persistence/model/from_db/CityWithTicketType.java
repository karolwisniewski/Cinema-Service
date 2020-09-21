package com.app.persistence.model.from_db;

import com.app.persistence.model.enums.TicketTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CityWithTicketType {

    private String city;
    private TicketTypeEnum ticket_type;
    private Integer ticket_counter;

}
