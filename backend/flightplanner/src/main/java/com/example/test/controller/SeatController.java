package com.example.test.controller;
import com.example.test.assets.SeatFilter;
import com.example.test.dto.FlightDTO;
import com.example.test.dto.SeatDTO;
import com.example.test.service.SeatService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/api/seats")
@RestController
public class SeatController {
    private final SeatService seatService;


    @PostMapping("/getSeats")
    public ResponseEntity<List<SeatDTO>> getSeats(
        @RequestParam Integer seatCount,
        @RequestParam Integer planeId,
        @RequestParam Integer flightId) {

        List<SeatDTO> seats = seatService.generateAndRecommendSeats(seatCount, planeId, flightId);
        return ResponseEntity.ok(seats);
    }
    @GetMapping("/getSeatsByFlight")
    public ResponseEntity<Map<String, List<SeatDTO>>> getSeatsByFlight(
        @RequestParam Integer flightId,
        @RequestParam Integer planeId) {

        List<SeatDTO> allSeats = seatService.getSeatsByFlight(flightId, planeId);

        Map<String, List<SeatDTO>> seatGroups = new HashMap<>();
        seatGroups.put("availableSeats", allSeats.stream().filter(seat -> seat.getAvailable() && !seat.getRecommended()).toList());
        seatGroups.put("bookedSeats", allSeats.stream().filter(seat -> !seat.getAvailable() && !seat.getRecommended()).toList());
        seatGroups.put("recommendedSeats", allSeats.stream().filter(SeatDTO::getRecommended).toList());  // Täiendav kontroll siin!

        return ResponseEntity.ok(seatGroups);
    }
    @PostMapping("/filters")
    public ResponseEntity<List<SeatDTO>> getFilteredSeats(
        @RequestParam Integer flightId,
        @RequestParam Integer planeId,
        @RequestParam List<SeatFilter> filters) {
        System.out.println(filters);
        try {
            List<SeatDTO> filteredSeats = seatService.getSeatsWithFilters(flightId, planeId, filters);
            System.out.println(filteredSeats);
            return ResponseEntity.ok(filteredSeats);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
