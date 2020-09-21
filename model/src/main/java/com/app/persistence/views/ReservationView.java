package com.app.persistence.views;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ReservationView {

    private Integer seance_id;
    private String movie_title;
    private String cinema_name;
    private String room_name;
    private LocalDateTime screening_date;
    private Integer seat_row;
    private Integer seat_column;

    @Override
    public String toString() {
        return "Reservation on: "+ movie_title +
                " in " + cinema_name +
                " room: " + room_name +
                " time: " + screening_date +
                " seat(R: " + seat_row +
                ", C " + seat_column +
                ") ";
    }
}
