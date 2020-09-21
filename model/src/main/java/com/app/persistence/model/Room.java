package com.app.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Room {

    private Integer id;
    private Integer cinemaId;
    private String name;
    private Integer rowsNumber;
    private Integer columnsNumber;

}

// insert into table () values (....)
// insert into table () values (....), (....), (....)
