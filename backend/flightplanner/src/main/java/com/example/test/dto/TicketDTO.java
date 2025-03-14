package com.example.test.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDTO {
    private Integer id;
    private String seatId;
    private Integer flightId;
}
