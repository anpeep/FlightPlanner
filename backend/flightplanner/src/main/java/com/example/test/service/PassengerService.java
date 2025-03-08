package com.example.test.service;

import com.example.test.dto.TicketDTO;
import com.example.test.mapping.PassengerMapper;
import com.example.test.mapping.TicketMapper;
import com.example.test.model.Ticket;
import com.example.test.repository.PassengerRepository;
import com.example.test.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerService {
//    private final PassengerRepository passengerRepository;
//    private final PassengerMapper passengerMapper;
//    private final TicketRepository ticketRepository;
//    private final TicketMapper ticketMapper;

//    private void addTicket(TicketDTO ticket) {
//        Ticket tickets = ticketRepository.findByClient(ticket.getClient());
//        Ticket tickets = ticketMapper.toEntity(ticket);
//        tickets.setClient(client);
//        Ticket savedTickets = ticketRepository.save(tickets);
//        return List.of(ticketMapper.toDTO(savedTickets));
//    }
//    private List<TicketDTO> getClientTickets(TicketDTO ticket) {
//        List<Ticket> tickets = ticketRepository.findByClient(ticket.getPassenger());
//        return tickets.stream()
//            .map(ticketMapper::toDTO)
//            .toList();
//    }


}
