package com.example.test.service;

import com.example.test.dto.SeatDTO;
import com.example.test.model.Plane;
import com.example.test.model.Seat;
import com.example.test.model.Ticket;
import com.example.test.repository.FlightRepository;
import com.example.test.repository.PlaneRepository;
import com.example.test.repository.SeatRepository;
import com.example.test.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        if (seatRepository.countByPlaneId(planeId) == 0) {
            List<Seat> newSeats = new ArrayList<>();
            int totalRows = 11;
            int totalColumns = 8;

            for (int row = 1; row <= totalRows; row++) {
                List<String> seatPositions = new ArrayList<>();

                // Erinevad paigutused vastavalt reale
                if (row == 1) {
                    seatPositions = List.of("C", "D", "E", "F");  // Esimene rida
                } else if (row == 5 || row == 11) {
                    seatPositions = List.of("D", "E");  // 5. ja 11. rida
                } else {
                    seatPositions = List.of("A", "B", "C", "D", "E", "F", "G", "H");  // Kõik teised read
                }
                for (String pos : seatPositions) {
                    Seat seat = new Seat();
                    seat.setPlane(plane);
                    seat.setRecommended(true);
                    seat.setRow(row);
                    seat.setSeat_column(pos);
                    seat.setAvailable(true);
                    newSeats.add(seat);
                }
            }
            int totalSeats = totalRows * totalColumns;
            int bookedSeatsCount = random.nextInt(totalSeats - seatCount);
            Collections.shuffle(newSeats);

            for (int i = 0; i < bookedSeatsCount + 1; i++) {
                newSeats.get(i).setAvailable(false);
            }

            seatRepository.saveAll(newSeats);
        }
        List<Seat> availableSeats = seatRepository.findByPlaneId(planeId).stream()
            .filter(Seat::getAvailable)
            .sorted(Comparator.comparingInt(Seat::getRow)
                .thenComparing(seat -> seat.getSeat_column().charAt(0))).toList();

        List<Seat> bookedSeats = seatRepository.findByPlaneId(planeId).stream()
            .filter(seat -> !seat.getAvailable())
            .toList();

        // Broneeri soovitatud kohad ja loo piletid
        List<Seat> recommendedSeats = new ArrayList<>();
        Map<Integer, List<Seat>> seatsByRow = availableSeats.stream()
            .collect(Collectors.groupingBy(Seat::getRow));

        for (List<Seat> rowSeats : seatsByRow.values()) {
            if (recommendedSeats.size() >= seatCount) break;

            for (int i = 0; i <= rowSeats.size() - seatCount; i++) {
                List<Seat> candidateSeats = rowSeats.subList(i, i + seatCount);

                if (candidateSeats.size() == seatCount) {
                    recommendedSeats.addAll(candidateSeats);
                    break;
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

        seatDTOs.addAll(bookedSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(false)
                .recommended(false)
                .build())
            .toList());

        // Soovitatud toolid
        seatDTOs.addAll(recommendedSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(false)
                .recommended(true)
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



    public List<Seat> applyFiltersAndRecommendSeats(Integer planeId, Integer flightId, List<Integer> filters) {
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);
        List<Seat> availableSeats = allSeats.stream().filter(Seat::getAvailable).collect(Collectors.toList());  // Saadaval olevad toolid

        // Rakendame valitud filtreid
        allSeats.stream().filter(Seat::getAvailable).forEach(seat -> {

            boolean matchesAllFilters = true; // Alustame tõene kõikide filtrite jaoks

            // Akna tooli filter
            if (filters.contains(1) && !seat.getSeat_column().equals("A") && !seat.getSeat_column().equals("F")) {
                matchesAllFilters = false;
                seat.setRecommended(false); // Eemaldame soovituse
            }

            // Väljapääsu filter
            if (filters.contains(2) && (seat.getRow() != 10 && seat.getRow() != 5)) {
                matchesAllFilters = false;
                seat.setRecommended(false); // Eemaldame soovituse
            }

            // Väljapääsu ja akna toolide ristmik
            if (filters.contains(3) && !(seat.getRow() == 10 || seat.getRow() == 5) &&
                !(seat.getSeat_column().equals("A") || seat.getSeat_column().equals("F"))) {
                matchesAllFilters = false;
                seat.setRecommended(false); // Eemaldame soovituse
            }
            // Kui kõik filtrid sobivad, siis lubame tooli soovituse
            if (matchesAllFilters) {
                seat.setRecommended(true);  // Soovitage tool
            }
        });

        // Tagastame kõik toolid, millel on uus soovitus
        return allSeats.stream()
            .filter(Seat::getRecommended)  // Kui tool on soovitatud, siis lisame selle
            .collect(Collectors.toList());
    }

}
