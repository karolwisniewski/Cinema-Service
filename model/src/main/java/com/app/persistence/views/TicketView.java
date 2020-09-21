package com.app.persistence.views;

import com.app.persistence.model.enums.Status;
import com.app.persistence.model.enums.TicketTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TicketView {
    private String movie_title;
    private String cinema_name;
    private String room_name;
    private LocalDateTime screening_date;
    private BigDecimal price;
    private Integer seat_row;
    private Integer seat_column;
    private TicketTypeEnum ticket_type;
    private Status status;

    @Override
    public String toString() {
        return  "Ticket on: "+ movie_title +
                " in " + cinema_name +
                " room: " + room_name +
                " time: " + screening_date +
                " price: " + price +
                " seat(R: " + seat_row +
                ", C: " + seat_column +
                ") type of ticket: " + ticket_type +
                 " status: " + status;
    }
}
