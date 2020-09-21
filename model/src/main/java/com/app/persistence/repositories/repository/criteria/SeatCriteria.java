package com.app.persistence.repositories.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SeatCriteria {

    Integer seanceId;
    Integer row;
    Integer column;

}
