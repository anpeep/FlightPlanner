package com.example.test.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class FlightDTO {
    private Integer id;
    private String departureCity;
    private String arrivalCity;
    private Instant departOn;
    private Instant arriveOn;
    private Integer planeId;
    private Float price;

}
