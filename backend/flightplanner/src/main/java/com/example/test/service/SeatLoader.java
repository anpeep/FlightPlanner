package com.example.test.service;

import com.example.test.assets.Seating;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import com.example.test.assets.Seating;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SeatLoader {
    public static List<Seating> loadSeatsFromJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = SeatLoader.class.getResourceAsStream("/Seat.json");

        if (inputStream == null) {
            throw new IOException("Seat.json not found");
        }

        return objectMapper.readValue(inputStream, new TypeReference<>() {
        });
    }
}
