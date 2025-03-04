package com.example.test.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatDTO {
    private Long id;
    private float price;
    private boolean available;
    private boolean windowSeat;
    private boolean legRoom;
    private boolean recline;
    private boolean wheelChair;
    private boolean nearBoarding;
}
