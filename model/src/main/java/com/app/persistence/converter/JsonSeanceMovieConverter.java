package com.app.persistence.converter;

import com.app.persistence.converter.generic.JsonConverter;
import com.app.persistence.dto.CreateMovieAndSeanceDto;

import java.util.List;

public class JsonSeanceMovieConverter extends JsonConverter<List<CreateMovieAndSeanceDto>> {
    public JsonSeanceMovieConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
