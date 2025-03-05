package com.example.test.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlaneDTO {
    private Long id;
    private String airport;
    private int departureTime;
    private int arrivalTime;
    private List<SeatDTO> seats;
}
