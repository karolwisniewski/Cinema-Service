package com.app.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Seance {

    private Integer id;
    private Integer movieId;
    private Integer roomId;
    private LocalDateTime screeningDate;

}
