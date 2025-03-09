package com.example.test.controller;
import com.example.test.dto.FlightDTO;
import com.example.test.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("flight")
@RestController
public class FlightController {
    private FlightService flightService;


    @PostMapping("book")
    public ResponseEntity<FlightDTO> createFlight(@RequestBody FlightDTO flightDTO) {
        FlightDTO flight = flightService.createFlight(flightDTO);
        return new ResponseEntity<>(flight, HttpStatus.OK);
    }


    @GetMapping("booked")
    public ResponseEntity<FlightDTO> getFlight(@PathVariable("id") Integer id) {
        FlightDTO flight = flightService.getFlightById(id);
        return new ResponseEntity<>(flight, HttpStatus.OK);
    }


}
