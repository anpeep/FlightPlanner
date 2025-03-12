package com.example.test.service;
import com.example.test.assets.Seating;
import com.example.test.dto.SeatDTO;
import com.example.test.mapping.SeatMapper;
import com.example.test.model.Plane;
import com.example.test.model.Seat;
import com.example.test.assets.SeatFilter;
import com.example.test.repository.PlaneRepository;
import com.example.test.repository.SeatRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final PlaneRepository planeRepository;
    private final Random random = new Random();
    public List<SeatDTO> bookRandomSeats(Integer seatCount, Integer planeId) {
        System.out.println("heeee" + planeId);

        // ✅ Leia lennuk ID järgi
        Plane plane = planeRepository.findById(planeId)
            .orElseThrow(() -> new IllegalArgumentException("Plane not found with ID: " + planeId));

        // ✅ Kontrolli, kas lennukil on juba toolid andmebaasis
        if (seatRepository.countByPlaneId(planeId) == 0) {
            List<Seat> newSeats = new ArrayList<>();
            int totalRows = 10;
            int totalColumns = 6;

            for (int row = 1; row <= totalRows; row++) {
                for (char col = 'A'; col < 'A' + totalColumns; col++) {
                    Seat seat = new Seat();
                    seat.setPlane(plane);
                    seat.setRow(row);
                    seat.setSeat_column(String.valueOf(col));
                    seat.setAvailable(true);
                    newSeats.add(seat);
                }
            }
            int totalSeats = totalRows * totalColumns;  // Kokku toolide arv
            int bookedSeatsCount = random.nextInt(totalSeats - seatCount); // Suvaline arv 0 kuni (78 - seatCount)

            // Segame toolid juhuslikult
            Collections.shuffle(newSeats);

            // Broneerime suvalised toolid
            for (int i = 0; i < bookedSeatsCount; i++) {
                newSeats.get(i).setAvailable(false);  // Märgime need toolid broneerituks
            }

            seatRepository.saveAll(newSeats);
            System.out.println("Generated and booked " + bookedSeatsCount + " seats for plane: " + planeId);
        }

        // ✅ Leia kõik toolid selle lennuki jaoks
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);

        return allSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(seat.getAvailable())
                .build())
            .collect(Collectors.toList());
    }



    private List<Seat> findBestAdjacentSeats(List<Seat> availableSeats, int seatCount) {
        for (int i = 0; i <= availableSeats.size() - seatCount; i++) {
            List<Seat> potentialSeats = availableSeats.subList(i, i + seatCount);
            boolean isAdjacent = true;

            for (int j = 1; j < seatCount; j++) {
                if (!areSeatsAdjacent(potentialSeats.get(j - 1), potentialSeats.get(j))) {
                    isAdjacent = false;
                    break;
                }
            }

            if (isAdjacent) {
                return potentialSeats;
            }
        }
        return availableSeats.subList(0, seatCount);
    }
    private boolean areSeatsAdjacent(Seat seat1, Seat seat2) {
        return seat1.getRow().equals(seat2.getRow()) &&
            Math.abs(seat1.getSeat_column().charAt(0) - seat2.getSeat_column().charAt(0)) == 1;
    }
    public List<SeatDTO> getAllSeats() {
        System.out.println("honey im home");
        return seatRepository.findAll().stream()
            .map(seat -> SeatDTO.builder().row(seat.getRow()).id(seat.getId()).planeId(seat.getPlane().getId()).seat_column(seat.getSeat_column()).row(seat.getRow()).available(seat.getAvailable()).build()).toList();
    }
}
