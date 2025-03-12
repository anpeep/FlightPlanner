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
@RequestMapping("/api/seats")
@RestController
public class SeatController {
    private final SeatService seatService;


    @PostMapping("/getAvailableSeats")
    public ResponseEntity<List<SeatDTO>> generateBookedSeats(
        @RequestParam Integer seatCount,
        @RequestParam Integer planeId) {
        System.out.println(seatCount);
        System.out.println(planeId);

        List<SeatDTO> seats = seatService.bookRandomSeats(seatCount, planeId);
        return new ResponseEntity<>(seats, HttpStatus.OK);
    }


    @GetMapping()
    public ResponseEntity<List<SeatDTO>> getAllSeats() {
        return ResponseEntity.ok(seatService.getAllSeats());
    }

}
