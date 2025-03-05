package com.example.test.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientDTO {
    private Long id;
    private List<TicketDTO> tickets;
}
