package com.app.persistence.converter;

import com.app.persistence.converter.generic.JsonConverter;
import com.app.persistence.views.TicketView;

import java.util.List;

public class JsonTicketViewConverter extends JsonConverter<List<TicketView>> {
    public JsonTicketViewConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
