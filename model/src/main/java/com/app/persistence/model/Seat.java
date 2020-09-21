package com.app.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Seat {

    private Integer id;
    private Integer roomId;
    private Integer roww;
    private Integer columnn;

}
