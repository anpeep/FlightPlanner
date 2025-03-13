package com.example.test.service;
import com.example.test.assets.Seating;
import com.example.test.dto.SeatDTO;
import com.example.test.dto.TicketDTO;
import com.example.test.mapping.SeatMapper;
import com.example.test.mapping.TicketMapper;
import com.example.test.model.Flight;
import com.example.test.model.Plane;
import com.example.test.model.Seat;
import com.example.test.assets.SeatFilter;
import com.example.test.model.Ticket;
import com.example.test.repository.FlightRepository;
import com.example.test.repository.PlaneRepository;
import com.example.test.repository.SeatRepository;
import com.example.test.repository.TicketRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final PlaneRepository planeRepository;
    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;
    private final Random random = new Random();

    public List<SeatDTO> generateAndRecommendSeats(Integer seatCount, Integer planeId, Integer flightId) {
        Plane plane = planeRepository.findById(planeId)
            .orElseThrow(() -> new IllegalArgumentException("Plane not found with ID: " + planeId));

        // Kui lennuki toolid puuduvad, genereeri need
        if (seatRepository.countByPlaneId(planeId) == 0) {
            List<Seat> newSeats = new ArrayList<>();
            int totalRows = 10;
            int totalColumns = 6;

            for (int row = 1; row <= totalRows; row++) {
                for (char col = 'A'; col < 'A' + totalColumns; col++) {
                    Seat seat = new Seat();
                    seat.setPlane(plane);
                    seat.setRecommended(true);
                    seat.setRow(row);
                    seat.setSeat_column(String.valueOf(col));
                    seat.setAvailable(true);
                    newSeats.add(seat);
                }
            }

            // Suvaliselt broneeri osa toolidest
            int totalSeats = totalRows * totalColumns;
            int bookedSeatsCount = random.nextInt(totalSeats - seatCount);
            Collections.shuffle(newSeats);

            for (int i = 0; i < bookedSeatsCount; i++) {
                newSeats.get(i).setAvailable(false);
            }

            seatRepository.saveAll(newSeats);
        }
        List<Seat> availableSeats = seatRepository.findByPlaneId(planeId).stream()
            .filter(Seat::getAvailable)
            .sorted(Comparator.comparingInt(Seat::getRow)
                .thenComparing(seat -> seat.getSeat_column().charAt(0))) // Sorteeri rea ja veeru järgi
            .toList();


        // Leia broneeritud toolid
        List<Seat> bookedSeats = seatRepository.findByPlaneId(planeId).stream()
            .filter(seat -> !seat.getAvailable())
            .toList();

        // Broneeri soovitatud kohad ja loo piletid
        List<Seat> recommendedSeats = new ArrayList<>();
        Map<Integer, List<Seat>> seatsByRow = availableSeats.stream()
            .collect(Collectors.groupingBy(Seat::getRow));

        for (List<Seat> rowSeats : seatsByRow.values()) {
            if (recommendedSeats.size() >= seatCount) break; // Kui juba piisavalt leitud, lõpeta

            for (int i = 0; i <= rowSeats.size() - seatCount; i++) {
                List<Seat> candidateSeats = rowSeats.subList(i, i + seatCount);

                if (candidateSeats.size() == seatCount) {
                    recommendedSeats.addAll(candidateSeats);
                    break; // Leia ainult üks komplekt samast reast
                }
            }
        }
        if (recommendedSeats.size() < seatCount) {
            for (Seat seat : availableSeats) {
                if (!recommendedSeats.contains(seat)) {
                    recommendedSeats.add(seat);
                    if (recommendedSeats.size() >= seatCount) break;
                }
            }
        }
        List<Ticket> recommendTickets = new ArrayList<>();

        for (int i = 0; i < seatCount; i++) {
            Seat seat = availableSeats.get(i);
            seat.setAvailable(false);
            recommendedSeats.add(seat);

            Ticket ticket = new Ticket();
            ticket.setSeat(seat);
            ticket.setFlight(flightRepository.findById(flightId).orElseThrow());
            ticket.setPrice(random.nextFloat() * 100);
            recommendTickets.add(ticket);
        }

        ticketRepository.saveAll(recommendTickets);
        seatRepository.saveAll(recommendedSeats);

        // Koosta tagastatav DTO list
        List<SeatDTO> seatDTOs = new ArrayList<>();

        seatDTOs.addAll(availableSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(true)
                .recommended(false)  // Saadaval tool ei ole soovitatud
                .build())
            .toList());

        // Broneeritud toolid
        seatDTOs.addAll(bookedSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(false)  // Broneeritud tool
                .recommended(false)  // Broneeritud tool ei ole soovitatud
                .build())
            .toList());

        // Soovitatud toolid
        seatDTOs.addAll(recommendedSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(false)  // Soovitatud toolid on broneeritud
                .recommended(true)  // Märgime toolid soovitatuks
                .build())
            .toList());

        return seatDTOs;
    }

    public List<SeatDTO> getSeatsByFlight(Integer flightId, Integer planeId) {
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);

        return allSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(seat.getAvailable())
                .recommended(isRecommendedSeat(seat, flightId))
                .build())
            .toList();
    }

    private boolean isRecommendedSeat(Seat seat, Integer flightId) {
        return ticketRepository.existsBySeatIdAndFlightId(seat.getId(), flightId);
    }


    // ✅ Kas koht on akna ääres? (rea esimene või viimane iste)
    private boolean isWindowSeat(Seat seat) {
        String column = seat.getSeat_column();
        return column.equals("A") || column.equals("F"); // Lennukitel on akna ääres tavaliselt A ja F
    }

    // ✅ Kas koht on väljapääsureas?
    private boolean isExitRow(Seat seat) {
        return seat.getRow() == 10 || seat.getRow() == 20; // Oletame, et väljapääsuread on 10 ja 20
    }
    public List<SeatDTO> getSeatsWithFilters(Integer flightId, Integer planeId, List<SeatFilter> filters) throws IOException {
        // 1️⃣ Lae JSON andmed
        List<Seating> seatings = SeatLoader.loadSeatsFromJson();

        // 2️⃣ Loo kaardistus (key: "row + column", value: Seating)
        Map<String, Seating> seatingMap = seatings.stream()
            .collect(Collectors.toMap(s -> s.getRow() + s.getColumn(), s -> s));

        // 3️⃣ Filtreeri istmed, mis vastavad JSON filtritele
        return seatRepository.findByPlaneId(planeId).stream()
            .filter(seat -> {
                Seating seating = seatingMap.getOrDefault(seat.getRow() + seat.getSeat_column(), null);
                return seating != null && filters.contains(seating.getFilter());
            })
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(seat.getAvailable())
                .recommended(false)
                .build())
            .toList();
    }

    private List<Seat> findBestAdjacentSeats(List<Seat> seats, int seatCount) {
        Map<Integer, List<Seat>> seatsByRow = seats.stream()
            .collect(Collectors.groupingBy(Seat::getRow));

        for (List<Seat> rowSeats : seatsByRow.values()) {
            if (rowSeats.size() >= seatCount) {
                return rowSeats.subList(0, seatCount); // Võta järjestikused toolid
            }
        }

        return seats.subList(0, Math.min(seats.size(), seatCount)); // Kui pole järjestikuseid, võta lähimad
    }
}
