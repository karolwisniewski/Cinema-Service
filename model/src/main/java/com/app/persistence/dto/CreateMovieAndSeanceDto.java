package com.app.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateMovieAndSeanceDto {
    private CreateMovieDto movieDto;
    private Integer roomId;
    private LocalDateTime screeningDate;
}
