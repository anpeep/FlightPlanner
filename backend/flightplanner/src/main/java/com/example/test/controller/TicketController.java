package com.example.test.controller;

import com.example.test.dto.FlightDTO;
import com.example.test.dto.TicketDTO;
import com.example.test.service.StartService;
import com.example.test.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/tickets")
@RestController
public class TicketController {
//    private final TicketService ticketService;
//    @GetMapping()
//    public ResponseEntity<List<TicketDTO>> getTickets(@RequestParam Integer flightId, Integer ticketCount) {
//        List<TicketDTO> tickets = ticketService.getTicketsByFlightId(flightId, ticketCount);
//        return ResponseEntity.ok(tickets);
//    }
}
