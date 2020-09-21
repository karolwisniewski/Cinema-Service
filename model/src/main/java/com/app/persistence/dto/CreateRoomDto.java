package com.app.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateRoomDto {
    private String name;
    private Integer rowsNumber;
    private Integer columnsNumber;
}
