package com.example.test.dto;

import com.example.test.model.Passenger;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDTO {
    private Long id;
    private float price;
    private boolean available;
    private boolean windowSeat;
    private boolean legRoom;
    private boolean wheelChair;
    private boolean nearBoarding;
    private Passenger passenger;
    private int departureTime;
    private int arrivalTime;
}
