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
    public List<SeatDTO> bookRandomSeats(Integer seatCount, Integer planeId) {
        Plane plane = planeRepository.findById(planeId)
            .orElseThrow(() -> new IllegalArgumentException("Plane not found with ID: " + planeId));

        if (seatRepository.countByPlaneId(planeId) == 0) {
            List<Seat> newSeats = new ArrayList<>();
            int totalRows = 10;
            int totalColumns = 6;

            for (int row = 1; row <= totalRows; row++) {
                for (char col = 'A'; col < 'A' + totalColumns; col++) {
                    Seat seat = new Seat();
                    seat.setPlane(plane);
                    seat.setRow(row);
                    seat.setRecommended(false);
                    seat.setSeat_column(String.valueOf(col));
                    seat.setAvailable(true);
                    newSeats.add(seat);
                }
            }
            int totalSeats = totalRows * totalColumns;
            int bookedSeatsCount = random.nextInt(totalSeats - seatCount);
            Collections.shuffle(newSeats);

            for (int i = 0; i < bookedSeatsCount; i++) {
                newSeats.get(i).setAvailable(false);
            }
            seatRepository.saveAll(newSeats);
        }
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);

        return allSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .recommended(false)
                .seat_column(seat.getSeat_column())
                .available(seat.getAvailable())
                .build())
            .collect(Collectors.toList());
    }

    public List<SeatDTO> recommendSeats(Integer seatCount, Integer flightId) {
        List<Seat> availableSeats = seatRepository.findAll().stream()
            .filter(Seat::getAvailable)
            .toList();

        List<Seat> bookedSeats = seatRepository.findAll().stream()
            .filter(seat -> !seat.getAvailable())
            .toList();

        List<Seat> recommendedSeats = new ArrayList<>();

        List<Ticket> recommendTickets = new ArrayList<>();
        for (int i = 0; i < seatCount; i++) {
            Seat seat = availableSeats.get(i);
            seat.setRecommended(false);
            seat.setAvailable(true);
            recommendedSeats.add(seat);

            Ticket ticketDTO = new Ticket();
            ticketDTO.setSeat(seat);
            ticketDTO.setFlight(flightRepository.findById(flightId).orElseThrow());
            ticketDTO.setPrice(random.nextFloat() * 100);
            recommendTickets.add(ticketDTO);
        }
        ticketRepository.saveAll(recommendTickets);
        seatRepository.saveAll(recommendedSeats);

        List<SeatDTO> seatDTOs = new ArrayList<>();

        // Lisa saadaval olevad toolid (available)
        seatDTOs.addAll(availableSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .recommended(false)
                .available(true)  // Saadaval olevad
                .build())
            .toList());

        // Lisa broneeritud toolid (not available)
        seatDTOs.addAll(bookedSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .recommended(false)
                .available(false)  // Broneeritud toolid
                .build())
            .toList());

        seatDTOs.addAll(recommendedSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .recommended(true)
                .available(false)  // Märgime soovitatud toolid broneerituks
                .build())
            .toList());

        return seatDTOs;
    }

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

        // Leia saadaval olevad toolid
        List<Seat> availableSeats = seatRepository.findByPlaneId(planeId).stream()
            .filter(Seat::getAvailable)
            .toList();

        // Leia broneeritud toolid
        List<Seat> bookedSeats = seatRepository.findByPlaneId(planeId).stream()
            .filter(seat -> !seat.getAvailable())
            .toList();

        // Broneeri soovitatud kohad ja loo piletid
        List<Seat> recommendedSeats = new ArrayList<>();
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

}
