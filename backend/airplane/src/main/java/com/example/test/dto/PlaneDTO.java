package com.example.test.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaneDTO {
    private Long id;
    private String airport;
    private int departureTime;
    private int arrivalTime;
}
