package com.example.test.controller;

import com.example.test.dto.FlightDTO;
import com.example.test.mapping.FlightMapper;
import com.example.test.model.Flight;
import com.example.test.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/details")
    public ResponseEntity<FlightDTO> getFlightDetails(@RequestParam Integer planeId, @RequestParam Integer flightId) {
        Optional<Flight> flightOptional = flightRepository.findById(flightId);
        if (flightOptional.isPresent()) {
            Flight flight = flightOptional.get();
            return ResponseEntity.ok(flightMapper.toDTO(flight));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
