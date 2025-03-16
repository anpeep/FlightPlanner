package com.example.test.service;

import com.example.test.dto.FlightDTO;
import com.example.test.dto.TicketDTO;
import com.example.test.mapping.TicketMapper;
import com.example.test.model.Ticket;
import com.example.test.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
//    private final TicketRepository ticketRepository;
//    private final TicketMapper ticketMapper;
//
//    public List<TicketDTO> getTicketsByFlightId(Integer flightId, Integer ticketCount) {
//        // Oletame, et TicketRepository otsib lennu ID järgi pileteid
//        List<Ticket> tickets = ticketRepository.findByFlightId(flightId);
//
//        // Kui on määratud piletite arv, siis piira tulemusi
//        if (ticketCount != null && ticketCount > 0) {
//
//            tickets = tickets.stream().limit(ticketCount).toList();
//        }
//
//        // Muuda Ticket objektid DTO-ks
//        return tickets.stream().map(ticketMapper::toDTO).toList();
//    }
}
