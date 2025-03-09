package com.example.test.controller;
import com.example.test.dto.FlightDTO;
import com.example.test.dto.SeatDTO;
import com.example.test.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("seat/")
@RestController
public class SeatController {
    private SeatService seatService;


    @PostMapping("create")
    public ResponseEntity<SeatDTO> createSeat(@RequestBody SeatDTO seatDTO) {
        SeatDTO seat = seatService.createSeat(seatDTO);
        return new ResponseEntity<>(seat, HttpStatus.OK);
    }


    @GetMapping("booked")
    public ResponseEntity<Optional<SeatDTO>> getSeat(@PathVariable("id") Integer id) {
        Optional<SeatDTO> seat = seatService.getSeatById(id);
        return new ResponseEntity<>(seat, HttpStatus.OK);
    }

}
