package com.example.test.service;
import com.example.test.dto.FlightDTO;
import com.example.test.mapping.FlightMapper;
import com.example.test.mapping.PlaneMapper;
import com.example.test.model.Flight;
import com.example.test.model.Plane;
import com.example.test.repository.FlightRepository;
import com.example.test.repository.PlaneRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
@RequiredArgsConstructor

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final PlaneRepository planeRepository;
    private final FlightMapper flightMapper;
    private final Random random = new Random();

    public List<FlightDTO> updateFlightCities(LocalDate date, String departureCity, String arrivalCity) {
        List<Plane> planes = planeRepository.findAll();

        if (planes.isEmpty()) {
            // If no planes are found, create a new one
            Plane newPlane = new Plane();
            planeRepository.save(newPlane);  // Save the new plane to the database

            // Add the new plane to the list of planes
            planes.add(newPlane);
        }

        int flightCount = random.nextInt(3) + 3; // Random number of flights (between 3 and 5)
        List<Flight> flights = new ArrayList<>();
        ZoneId zoneId = ZoneId.systemDefault();
        Instant baseDepartOn = date.atStartOfDay(zoneId).toInstant();

        // Generate flights
        for (int i = 0; i < flightCount; i++) {
            Plane selectedPlane = planes.get(random.nextInt(planes.size())); // Randomly select a plane

            Flight flight = new Flight();
            flight.setDepartOn(baseDepartOn.plus(random.nextInt(24), ChronoUnit.HOURS)); // Random departure time within 24 hours
            flight.setArriveOn(flight.getDepartOn().plus(random.nextInt(6) + 1, ChronoUnit.HOURS)); // Random flight duration between 1 and 6 hours
            flight.setDepartureCity(departureCity);
            flight.setArrivalCity(arrivalCity);
            flight.setPlane(selectedPlane); // Set the selected plane for the flight

            flights.add(flight);
        }

        flightRepository.saveAll(flights); // Save all the generated flights to the database

        // Convert the flights to FlightDTO and return them

        return flights.stream()
            .map(flightMapper::toDTO)
            .toList();
    }}
