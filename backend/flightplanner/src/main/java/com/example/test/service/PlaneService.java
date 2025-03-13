package com.example.test.service;

import com.example.test.dto.FlightDTO;
import com.example.test.dto.PlaneDTO;
import com.example.test.model.Plane;
import com.example.test.repository.PlaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaneService {

    private final PlaneRepository planeRepository;

    public PlaneDTO getFlightsForPlane(Integer planeId) {
        // Fetch the plane from the repository
        Plane plane = planeRepository.findById(planeId)
            .orElseThrow(() -> new NoSuchElementException("Plane not found with ID: " + planeId));

        // Manually map the Plane entity to PlaneDTO
        // Assuming planeId is a property of flight

        return PlaneDTO.builder()
            .id(plane.getId())
            .flights(plane.getFlights().stream()
                .map(flight -> FlightDTO.builder()
                    .id(flight.getId())
                    .departureCity(flight.getDepartureCity())
                    .arrivalCity(flight.getArrivalCity())
                    .departOn(flight.getDepartOn())
                    .arriveOn(flight.getArriveOn())
                    .planeId(flight.getPlane().getId()) // Assuming planeId is a property of flight
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
