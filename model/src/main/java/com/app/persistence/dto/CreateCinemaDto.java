package com.app.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class CreateCinemaDto {
    private String name;
    private String city;

    @Override
    public String toString() {
        return "Cinema " + name + " in " + city;
    }
}
