package com.example.test.service;

import com.example.test.dto.FlightDTO;
import com.example.test.mapping.FlightMapper;
import com.example.test.model.Flight;
import com.example.test.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;


    public List<FlightDTO> getFlightsByPlaneId(Integer planeId) {
        // Eeldame, et repository annab meile lennud planeId alusel
        List<Flight> flights = flightRepository.findByPlaneId(planeId);

        return flights.stream()
                .map(flightMapper::toDTO).toList();
    }
}
