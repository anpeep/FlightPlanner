package com.example.test.service;

import com.example.test.dto.TicketDTO;
import com.example.test.exceptions.NotFoundException;
import com.example.test.mapping.TicketMapper;
import com.example.test.model.Ticket;
import com.example.test.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {
    private  TicketRepository ticketRepository;
    private  TicketMapper ticketMapper;

    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticketRepository.save(ticket);
        return ticketMapper.toDTO(ticket);

    }

    public Optional<TicketDTO> getTicketById(Integer id) {
        return ticketRepository.findById(id).map(ticketMapper::toDTO);
    }
}
