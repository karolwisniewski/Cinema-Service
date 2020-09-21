package com.app.persistence.dto;

import com.app.persistence.model.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateMovieDto {
    private Integer id;
    private String title;
    private Category category;
    private LocalDate displaySince;
    private LocalDate displayTo;

    @Override
    public String toString() {
        return id + ". " + title + ", category=" + category +
                ", Screening(" + displaySince + ", " + displayTo + ")";
    }
}
