package com.example.test.controller;

import com.example.test.dto.SeatDTO;
import com.example.test.dto.TicketDTO;
import com.example.test.model.Ticket;
import com.example.test.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket")
public class TicketController {
//    public TicketController(TicketService ticketService) {
//        this.ticketService = ticketService;
//    }
//
//    private final TicketService ticketService;
//
//    // Otspunkt piletite määramiseks
//    @PostMapping("/generate")
//    public ResponseEntity<String> generateTickets() {
//        ticketService.generateTicketPrices();
//        return new ResponseEntity<>("Ticket prices generated successfully!", HttpStatus.CREATED);
//    }
//
//    // Otspunkt piletite saamiseks lennu ID ja klassi järgi
//    @GetMapping("/findByFlightAndClass")
//    public ResponseEntity<List<TicketDTO>> findTicketsByFlightAndClass(
//        @RequestParam TicketDTO ticketDTO) {
//        List<TicketDTO> tickets = ticketService.getTicketsByFlightAndClass(ticketDTO);
//        return new ResponseEntity<>(tickets, HttpStatus.OK);
//    }
}
