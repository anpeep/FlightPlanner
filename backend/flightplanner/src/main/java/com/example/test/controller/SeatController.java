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

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("seat/")
@RestController
public class SeatController {
    private final SeatService seatService;
    @PostConstruct
        public void init() {
        seatService.init();
    }
    @PostMapping("search")
    public ResponseEntity<List<SeatDTO>> findSeats(
        @RequestParam Integer planeId,
        @RequestParam Integer seatCount,
        @RequestParam(defaultValue = "NONE") SeatFilter seatFilter) {
        List<SeatDTO> seats = seatService.findAvailableSeats(planeId, seatCount, seatFilter);
        return new ResponseEntity<>(seats, HttpStatus.OK);
    }
    @PutMapping("filter")
    public ResponseEntity<List<SeatDTO>> findFilteredSeats(
        @RequestParam Integer planeId,
        @RequestParam Integer seatCount,
        @RequestParam SeatFilter seatFilter) {
        List<SeatDTO> seats = seatService.findAvailableSeats(planeId, seatCount, seatFilter);
        return new ResponseEntity<>(seats, HttpStatus.OK);
    }
    @PostMapping("book")
    public ResponseEntity<String> bookSeats(@RequestBody List<Integer> seatIds) {
        seatService.bookSeats(seatIds);
        return new ResponseEntity<>("Seats booked successfully!", HttpStatus.OK);
    }
}
