package com.app.persistence.repositories.repository.criteria;

import com.app.persistence.model.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieCriteria {
    private String title;
    private Category category;
    private String dateFrom;
    private String dateTo;
}
