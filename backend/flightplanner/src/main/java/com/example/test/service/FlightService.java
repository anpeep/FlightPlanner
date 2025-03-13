package com.example.test.service;

import com.example.test.dto.FlightDTO;
import com.example.test.mapping.FlightMapper;
import com.example.test.model.Flight;
import com.example.test.model.Plane;
import com.example.test.repository.FlightRepository;
import com.example.test.repository.PlaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
@RequiredArgsConstructor

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final PlaneRepository planeRepository;
    private final FlightMapper flightMapper;
    private final Random random = new Random();

    public List<FlightDTO> updateFlightCities(LocalDate date, String departureCity, String arrivalCity) {
        Plane plane = planeRepository.save(new Plane()); // Create & Save Plane First

        int flightCount = random.nextInt(3) + 3;
        List<Flight> flights = new ArrayList<>();
        ZoneId zoneId = ZoneId.systemDefault();
        Instant baseDepartOn = date.atStartOfDay(zoneId).toInstant();

        for (int i = 0; i < flightCount; i++) {
            Flight flight = new Flight();
            flight.setDepartOn(baseDepartOn.plus(random.nextInt(24), ChronoUnit.HOURS));
            flight.setArriveOn(flight.getDepartOn().plus(random.nextInt(6) + 1, ChronoUnit.HOURS));
            flight.setDepartureCity(departureCity);
            flight.setArrivalCity(arrivalCity);
            plane.setId(plane.getId());
            flight.setPlane(plane); // Seome lennuki lennuga
            flights.add(flight);
        }
        plane.setFlights(flights);
        flightRepository.saveAll(flights); // Salvesta lennud

        List<FlightDTO> d = flights.stream()
            .map(flightMapper::toDTO)
            .toList();

// Ensure planeId is set properly
        return d.stream()
            .peek(a -> {
                a.setPlaneId(plane.getId());
            })
            .toList();
    }
    }
