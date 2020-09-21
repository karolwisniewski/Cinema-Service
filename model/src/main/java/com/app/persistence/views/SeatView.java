package com.app.persistence.views;

import com.app.persistence.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SeatView {
    private Integer id;
    private Status status;
    private Integer roww;
    private Integer columnn;
}
