package com.example.test.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
@Data
@Builder
public class FlightDTO {
    private Integer id;
    private Integer seatId;
    private Integer plane_id;
    private String departure_city;
    private String arrival_city;
    private Instant departOn;
    private Instant arriveOn;
}
