package com.example.test.controller;
import com.example.test.dto.FlightDTO;
import com.example.test.service.FlightService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("flight")
@RestController
public class FlightController {
    private final FlightService flightService;

    @PostConstruct
    public void init() {
        flightService.generateRandomFlights();
    }

    @PostMapping("addCities")
    public ResponseEntity<String> addCities(@RequestParam String departureCity, @RequestParam String arrivalCity) {
        flightService.addCitiesToFlights(departureCity, arrivalCity);
        return new ResponseEntity<>("added", HttpStatus.OK);
    }
    @PutMapping("updateCities/{flightId}")
    public ResponseEntity<String> updateCities(
        @PathVariable Integer flightId,
        @RequestParam String departureCity,
        @RequestParam String arrivalCity) {

        try {
            flightService.updateFlightCities(flightId, departureCity, arrivalCity);  // Kutsume teenust
            return new ResponseEntity<>("Flight cities updated successfully!", HttpStatus.OK);  // Tagastame edukat vastust
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating flight cities: " + e.getMessage(), HttpStatus.BAD_REQUEST);  // Vea korral tagastame halb vastus
        }
    }
}
