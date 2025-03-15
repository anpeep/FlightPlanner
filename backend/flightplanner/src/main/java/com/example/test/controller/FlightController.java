package com.example.test.controller;

import com.example.test.dto.FlightDTO;
import com.example.test.mapping.FlightMapper;
import com.example.test.model.Flight;
import com.example.test.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/flight")
@RestController
@RequiredArgsConstructor
public class FlightController {
    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    @GetMapping
    public ResponseEntity<List<FlightDTO>> getAllFlights(@RequestParam Integer planeId) {
        List<Flight> flights = flightRepository.findByPlaneId(planeId);
        List<FlightDTO> flightDTOS = flights.stream().map(flightMapper::toDTO).toList();
        flightDTOS.forEach(flightDTO -> flightDTO.setPlaneId(planeId));
        return new ResponseEntity<>(flightDTOS, HttpStatus.OK);
        }
}
