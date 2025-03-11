package com.example.test.service;

import com.example.test.assets.Seating;
import com.example.test.dto.TicketDTO;
import com.example.test.mapping.TicketMapper;
import com.example.test.model.Flight;
import com.example.test.model.Seat;
import com.example.test.model.Ticket;
import com.example.test.repository.FlightRepository;
import com.example.test.repository.SeatRepository;
import com.example.test.repository.TicketRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
//    private final TicketRepository ticketRepository;
//    private final SeatRepository seatRepository;
//    private final FlightRepository flightRepository;
//    private final TicketMapper ticketMapper;
//    private final Map<String, Seating> seatclass;
//    private final Random random = new Random();

//    private void parseJson() {
//        try {
//            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Seats.json");
//            if (inputStream == null) {
//                throw new RuntimeException("Seats.json not found!");
//            }
//            String jsonData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
//            ObjectMapper objectMapper = new ObjectMapper();
//            List<Seating> seatFilters = objectMapper.readValue(jsonData, new TypeReference<>() {
//            });
//
//            // Täidame seatclass kaardi tüüpide põhjal
//            seatFilters.forEach(seat -> seatclass.put(seat.getType(), seat));  // seatclass on kaart, kus võti on type (nt "economy" või "business")
//
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read Seats.json", e);
//        }
//    }
//
//
//    public void generateTicketPrices() {
//        List<Seat> allSeats = seatRepository.findAll();
//
//        for (Seat seat : allSeats) {
//            for (Map.Entry<String, Seating> entry : seatclass.entrySet()) {
//                if (Objects.equals(seat.getId(), entry.getKey())) {
//                    float basePrice = randomPrice();
//                    if ("BUSINESS".equals(entry.getKey())) {
//                        basePrice = basePrice * 1.1f;
//                    }
//                    Ticket ticket = new Ticket();
//                    ticket.setSeat(seat);
//                    ticket.setFlight(seat.getPlane().getFlights().getFirst());
//                    ticket.setPrice(basePrice);
//                    ticketRepository.save(ticket);
//                }
//            }
//        }
//    }
//
//    // Abimeetod hindade määramiseks
//    private Float randomPrice() {
//        // Generaator põhineb valikul 100-500 hindade vahemikus
//        return 100 + random.nextFloat() * (500 - 100);
//    }
//
//    public List<TicketDTO> getTicketsByFlightAndClass(TicketDTO ticketDTO) {
//        // Leia kõik istmed vastava lennu jaoks
//        List<Seat> seats = seatRepository.findByFlightId(ticketDTO.getFlightId());
//
//        // Leia kõik piletid
//        List<Ticket> tickets = ticketRepository.findAll();
//
//        // Filtreeri piletid vastavalt seatClass väärtusele
//        return tickets.stream()
//            .filter(ticket -> {
//                // Otsime istme, mis on seotud pileti ID-ga
//                Optional<Seat> seatOptional = seats.stream()
//                    .filter(seat -> seat.equals(ticket.getSeat()))  // Kasutame ID-d, et leida õige iste
//                    .findFirst();  // Kasutame findFirst, et leida esimene vastav iste
//
//                return seatOptional
//                    .map(seat -> {
//                        // Eeldame, et 'seat.getId()' on seotud 'seatclass' kaardis oleva 'Seating' objektiga
//                        // Sealt saame välja võtta istme tüüp, et võrrelda piletite klassiga
//                        Seating seatingData = seatclass.get(seat.getId());  // Otsime seatclass kaardist istme tüübi järgi
//                        if (seatingData != null) {
//                            return Objects.equals(ticketDTO.getSeatId(), seatingData.getType());  // Võrdleme seatClass ja seatingData.getType()
//                        }
//                        return false;  // Kui seatingData ei leidu, siis ei ole vastavust
//                    })
//                    .orElse(false);  // Kui istet ei leita, siis tagastame false
//            })
//            .map(ticketMapper::toDTO)  // Piletid muudetakse TicketDTO-ks
//            .collect(Collectors.toList());  // Tagastame kõik sobivad piletid
 //   }
}
