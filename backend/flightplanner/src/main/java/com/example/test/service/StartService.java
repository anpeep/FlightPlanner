package com.example.test.service;

import com.example.test.Constants;
import com.example.test.model.Flight;
import com.example.test.model.Plane;
import com.example.test.repository.FlightRepository;
import com.example.test.repository.PlaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class StartService {
    private final PlaneRepository planeRepository;
    private final FlightRepository flightRepository;
    Random random = new Random();

    public Integer generateRandomFlights() {
        flightRepository.deleteAll();
        planeRepository.deleteAll();

        LocalDate today = LocalDate.now();
        LocalDate nextMonthSameDay = today.plusMonths(1);
        int daysBetween = (int) (nextMonthSameDay.toEpochDay() - today.toEpochDay());
        Plane plane = planeRepository.save(new Plane());
        List<Flight> flights = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            String departureCity = Constants.airports.get(random.nextInt(Constants.airports.size()));
            String arrivalCity = Constants.airports.get(random.nextInt(Constants.airports.size()));
            while (departureCity.equals(arrivalCity)) {
                arrivalCity = Constants.airports.get(random.nextInt(Constants.airports.size()));
            }
            LocalDate date = today.plusDays(random.nextInt(daysBetween + 1));
            int randomSeconds = random.nextInt((int) Duration.ofHours(24).getSeconds());
            Instant flightTime = date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).plusSeconds(randomSeconds);
            Instant arrivalTime = flightTime.plus(Duration.ofHours(random.nextInt(1, 10)));
            Float price = 50 + random.nextFloat() * 450;

            Flight flight = new Flight();
            flight.setDepartureCity(departureCity);
            flight.setArrivalCity(arrivalCity);
            flight.setDepartOn(flightTime);
            flight.setArriveOn(arrivalTime);
            flight.setPrice(price);
            flight.setPlane(plane);
            flights.add(flight);
        }
        plane.setFlights(flights);
        flightRepository.saveAll(flights);
        planeRepository.save(plane);

        return plane.getId();
    }


}
