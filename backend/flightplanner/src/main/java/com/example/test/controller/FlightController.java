package com.example.test.controller;

import com.example.test.dto.FlightDTO;
import com.example.test.mapping.FlightMapper;
import com.example.test.model.Flight;
import com.example.test.repository.FlightRepository;
import com.example.test.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/flight")
@RestController
@RequiredArgsConstructor
public class FlightController {
    private final FlightRepository flightRepository;
    private final FlightService flightService;
    private final FlightMapper flightMapper;
    @GetMapping
    public ResponseEntity<List<FlightDTO>> getAllFlights(@RequestParam Integer planeId) {
        List<Flight> flights = flightRepository.findByPlaneId(planeId);
        List<FlightDTO> flightDTOS = flights.stream().map(flightMapper::toDTO).toList();
        flightDTOS.forEach(flightDTO -> flightDTO.setPlaneId(planeId));
        return new ResponseEntity<>(flightDTOS, HttpStatus.OK);
        }

    @GetMapping("/details")
    public ResponseEntity<FlightDTO> getDetails(@RequestParam Integer planeId, @RequestParam Integer flightId) {
        Optional<Flight> flightOptional = flightRepository.findById(flightId);

        // Kontrollime, kas lend on olemas
        if (flightOptional.isPresent()) {
            Flight flight = flightOptional.get();  // Välja pakkuda Flight objekt
            return ResponseEntity.ok(flightMapper.toDTO(flight));  // Lend teisendatakse DTO-ks
        } else {
            return ResponseEntity.notFound().build();  // Kui lend ei ole olemas, tagastame 404 Not Found
        }
    }

}
