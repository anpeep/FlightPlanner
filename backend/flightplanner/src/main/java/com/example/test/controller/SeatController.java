package com.example.test.controller;

import com.example.test.dto.SeatDTO;
import com.example.test.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/seats")
@RestController
public class SeatController {
    private final SeatService seatService;

    @PostMapping()
    public ResponseEntity<List<SeatDTO>> getSeats(
        @RequestParam Integer planeId,
        @RequestParam Integer flightId) {

        List<SeatDTO> seats = seatService.generateBookedSeats(planeId, flightId);
        return ResponseEntity.ok(seats);
    }

    @GetMapping()
    public ResponseEntity<Map<String, List<SeatDTO>>> getSeatsByFlight(
        @RequestParam Integer flightId,
        @RequestParam Integer planeId) {
        Map<String, List<SeatDTO>> allSeats = seatService.getSeatsByFlight(flightId, planeId);

        return ResponseEntity.ok(allSeats);
    }

    @PostMapping("/addFilters")
    public ResponseEntity<List<SeatDTO>> setRecommendedSeats(
        @RequestParam Integer flightId,
        @RequestParam Integer seatCount,
        @RequestParam Integer planeId,
        @RequestBody List<Integer> filters) {
        List<SeatDTO> seats = seatService.
            addFilters(flightId, seatCount, planeId, filters);
        return ResponseEntity.ok(seats);
    }
}
