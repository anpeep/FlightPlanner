package com.example.test.service;
import com.example.test.dto.FlightDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service

@RequiredArgsConstructor
public class FlightService {
    public FlightDTO createFlight(FlightDTO flightDTO) {
        return flightDTO;

    }

    public FlightDTO getFlightById(Integer id) {
        return null;
    }
}
