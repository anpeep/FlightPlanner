package com.example.test.controller;

import com.example.test.dto.SeatDTO;
import com.example.test.dto.TicketDTO;
import com.example.test.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket")
public class TicketController {
    private  TicketService ticketService;

    @PostMapping("book")
    public ResponseEntity<TicketDTO> createTicket(@RequestBody TicketDTO ticketDTO) {
        TicketDTO ticket = ticketService.createTicket(ticketDTO);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @GetMapping("booked")
    public ResponseEntity<Optional<TicketDTO>> getTicket(@PathVariable("id") Integer id) {
        Optional<TicketDTO> ticket = ticketService.getTicketById(id);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }
}
