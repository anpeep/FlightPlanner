package com.example.test.controller;

import com.example.test.dto.FlightDTO;
import com.example.test.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/flight")
@RestController
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;

//    @GetMapping("/all")
//    public ResponseEntity<List<FlightDTO>> getAllFlights() {
//        return ResponseEntity.ok(flightService.getAllFlights());
//    }


    @PostMapping("/generate")
    public ResponseEntity<List<FlightDTO>> generateFlights(
        @RequestParam LocalDate date,
        @RequestParam String departureCity,
        @RequestParam String arrivalCity) {

        List<FlightDTO> flights = flightService.updateFlightCities(date, departureCity, arrivalCity);
        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

}
