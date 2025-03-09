package com.example.test.service;
import com.example.test.dto.FlightDTO;
import com.example.test.mapping.FlightMapper;
import com.example.test.model.Flight;
import com.example.test.model.Plane;
import com.example.test.repository.FlightRepository;
import com.example.test.repository.PlaneRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
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


    public List<FlightDTO> searchFlights(String departureCity, String arrivalCity) {
        List<Flight> flights = flightRepository.findAll();

        Flight selectedFlight = flights.get(random.nextInt(flights.size()));
        selectedFlight.setDepartureCity(departureCity);
        selectedFlight.setArrivalCity(arrivalCity);
        flightRepository.save(selectedFlight);

        return flightRepository.findAll().stream()
            .filter(f -> f.getDepartureCity() != null && f.getArrivalCity() != null)
            .map(flightMapper::toDTO)
            .toList();
    }


    public void generateRandomFlights() {
        List<Plane> planes = planeRepository.findAll();
        for (Plane plane : planes) {
            int flightsCount = random.nextInt(5) + 1;
            for (int i = 0; i < flightsCount; i++) {
                Flight flight = new Flight();
                flight.setPlane(plane);
                flight.setDepartureCity("City " + random.nextInt(10));
                flight.setArrivalCity("City " + random.nextInt(10));
                Instant departOn = generateRandomTimeInFuture();
                Instant arriveOn = departOn.plus(random.nextInt(10) + 1, ChronoUnit.HOURS);
                flight.setDepartOn(departOn);
                flight.setArriveOn(arriveOn);
                flightRepository.save(flight);
            }
        }
    }
    private Instant generateRandomTimeInFuture() {
        Instant now = Instant.now();
        long randomDays = random.nextInt(90) + 1;
        return now.plus(randomDays, ChronoUnit.DAYS);
    }
    public void addCitiesToFlights(String departureCity, String arrivalCity) {
        List<Flight> flights = flightRepository.findAll();
        for (Flight flight : flights) {
            if (flight.getDepartureCity() == null || flight.getArrivalCity() == null) {
                flight.setDepartureCity(departureCity);
                flight.setArrivalCity(arrivalCity);
                flightRepository.save(flight);
            }
        }
    }
    public void updateFlightCities(Integer flightId, String newDepartureCity, String newArrivalCity) {
        Optional<Flight> flightOptional = flightRepository.findById(flightId);

        if (flightOptional.isPresent()) {
            Flight flight = flightOptional.get();
            flight.setDepartureCity(newDepartureCity);  // Muudame sihtlinnu
            flight.setArrivalCity(newArrivalCity);  // Muudame saabumislinnu
            flightRepository.save(flight);  // Salvestame muudatused
        } else {
            throw new RuntimeException("Flight not found with ID: " + flightId);
        }
    }
}
