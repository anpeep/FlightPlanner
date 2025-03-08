package com.example.test.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDTO {
    private Integer id;
    private Integer seatId;
    private Integer flightId;
    private Float price;
}
