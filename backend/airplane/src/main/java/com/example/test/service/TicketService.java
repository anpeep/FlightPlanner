package com.example.test.service;

import com.example.test.dto.TicketDTO;
import com.example.test.mapping.TicketMapper;
import com.example.test.repository.ClientRepository;
import com.example.test.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ClientRepository clientRepository;
    private final TicketMapper ticketMapper;
    private float getTicketPrice(TicketDTO ticket) {
        //TODO
        return ticket.getPrice();
    }

    private List<TicketDTO> getBestTickets(TicketDTO ticket) {
        //TODO
        return Collections.emptyList();
    }
    private List<TicketDTO> changeTicket(TicketDTO ticket) {
        //TODO
        return Collections.emptyList();
    }
    private boolean deleteTicket(TicketDTO ticket) {
        if (ticketRepository.existsById(ticket.getId())) {
            ticketRepository.deleteById(ticket.getId());
            return true;
        }
        return false;
    }

}
