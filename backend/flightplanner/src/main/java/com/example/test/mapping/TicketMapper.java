package com.example.test.mapping;

import com.example.test.dto.TicketDTO;
import com.example.test.model.Ticket;
import org.mapstruct.Mapper;

@Mapper
public interface TicketMapper {
    TicketDTO toDTO(Ticket ticket);
    Ticket toEntity(TicketDTO ticketDTO);
}
