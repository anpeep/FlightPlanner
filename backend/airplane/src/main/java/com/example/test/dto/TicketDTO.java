package com.example.test.dto;

import com.example.test.model.Client;
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
    private boolean recline;
    private boolean wheelChair;
    private boolean nearBoarding;
    private Client client;
}
