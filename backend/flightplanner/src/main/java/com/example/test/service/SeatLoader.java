package com.example.test.service;

import com.example.test.assets.SeatFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SeatLoader {

    private static final Map<String, SeatFilter> seatfilters = new HashMap<>();

    public static Map<String, SeatFilter> loadSeatsFromJson() throws IOException {
        // Loeme JSON
        InputStream inputStream = SeatLoader.class.getResourceAsStream("/Seat.json");

        ObjectMapper objectMapper = new ObjectMapper();

        // Parseerime JSON-i JSONArray-ks
        assert inputStream != null;
        JSONArray jsonArray = new JSONArray(new String(inputStream.readAllBytes()));

        // Läbime iga JSON objekti
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // Välja tõmbamine filtriks
            SeatFilter filter = SeatFilter.valueOf(jsonObject.getString("filter"));

            // Kogume ka rida ja veerg
            int row = jsonObject.getInt("row");
            String column = jsonObject.getString("column");

            // Loome võtme ("row + column")
            String seatKey = row + column;

            // Salvestame kaardile
            seatfilters.put(seatKey, filter);
        }

        return seatfilters;
    }

    // Meetod, et saada seatfilters kaarti (kasutamiseks mujal)
    public static Map<String, SeatFilter> getSeatFilters() {
        return seatfilters;
    }
}
