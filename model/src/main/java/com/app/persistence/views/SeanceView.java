package com.app.persistence.views;

import com.app.persistence.model.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SeanceView {

    private Integer id;
    private Integer roomId;
    private String city;
    private String name;
    private String title;
    private Category category;
    private LocalDateTime screeningDate;

    @Override
    public String toString() {
        return  "Seance ID = " + id +
                ", Room ID = " + roomId +
                ", City = " + city +
                ", Cinema name = " + name +
                ", Movie title = " + title +
                ", Movie category = " + category +
                ", Seance date = " + screeningDate;
    }
}
