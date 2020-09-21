package com.app.persistence.repositories.repository.criteria;

import com.app.persistence.dto.CreateCinemaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SeanceCriteria {
    CreateCinemaDto cinema;
    String from;
    String to;
}
