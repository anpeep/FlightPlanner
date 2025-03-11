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
    public List<SeatDTO> bookRandomSeats(int seatCount, int flightId, int planeId) {
        // Find the plane using planeId
        Optional<Plane> planeOptional = planeRepository.findById(planeId);
        if (planeOptional.isEmpty()) {
            throw new IllegalArgumentException("Plane not found");
        }
        Plane plane = planeOptional.get();

        // Retrieve seats related to the flight and plane
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);

        // Filter available seats
        List<Seat> availableSeats = allSeats.stream()
            .filter(Seat::getAvailable)
            .sorted(Comparator.comparing(Seat::getRow).thenComparing(Seat::getSeat_column))
            .collect(Collectors.toList());

        if (availableSeats.size() < seatCount) {
            throw new IllegalArgumentException("Not enough available seats.");
        }

        List<Seat> bestSeats = findBestAdjacentSeats(availableSeats, seatCount);

        // Mark seats as booked
        bestSeats.forEach(seat -> seat.setAvailable(false));
        seatRepository.saveAll(bestSeats);

        return bestSeats.stream()
            .map(seat -> SeatDTO.builder()
                .row(seat.getRow())
                .id(seat.getId())
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
        return seatRepository.findAll().stream()
            .map(seat -> SeatDTO.builder().row(seat.getRow()).id(seat.getId()).planeId(seat.getId()).seat_column(seat.getSeat_column()).row(seat.getRow()).available(seat.getAvailable()).build()).toList();
    }
}
