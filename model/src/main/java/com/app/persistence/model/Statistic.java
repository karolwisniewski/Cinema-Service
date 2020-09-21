package com.app.persistence.model;

import com.app.persistence.model.enums.Category;
import com.app.persistence.model.enums.TicketTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Statistic {
    private String cityWithGreatestAudience;
    private Map<String, String> mostPopularMovieInCity;
    private Map<String, Category> mostPopularCategoryInCity;
    private Map<String, BigDecimal> averageTicketPriceInCity;
    private Map<String, BigDecimal> totalIncomeInCity;
    private Map<String, TicketTypeEnum> mostPopularTicketTypeInCity;

}
