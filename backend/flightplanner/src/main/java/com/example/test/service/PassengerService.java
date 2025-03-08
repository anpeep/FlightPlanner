package com.example.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
