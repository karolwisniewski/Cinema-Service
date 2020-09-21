package com.app.persistence.converter;

import com.app.persistence.converter.generic.JsonConverter;
import com.app.persistence.dto.CreateCinemaAndRoomsDto;
import java.util.List;

public class JsonCinemaRoomsConverter extends JsonConverter<List<CreateCinemaAndRoomsDto>> {
    public JsonCinemaRoomsConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
