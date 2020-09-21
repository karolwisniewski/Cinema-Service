package com.app.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CreateCinemaAndRoomsDto {
   private CreateCinemaDto cinema;
   private List<CreateRoomDto> rooms;
}
