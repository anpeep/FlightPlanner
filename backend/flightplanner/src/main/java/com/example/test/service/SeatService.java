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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final PlaneRepository planeRepository;
    private final Random random = new Random();

    public List<SeatDTO> generateAndRecommendSeats(Integer seatCount, Integer planeId, Integer flightId) {
        Plane plane = planeRepository.findById(planeId)
            .orElseThrow(() -> new IllegalArgumentException("Plane not found with ID: " + planeId));

        if (seatRepository.countByPlaneId(planeId) == 0) {
            List<Seat> newSeats = new ArrayList<>();
            int totalRows = 11;
            int totalColumns = 8;

            for (int row = 1; row <= totalRows; row++) {
                List<String> seatPositions = switch (row) {
                    case 1 -> List.of("C", "D", "E", "F");
                    case 5, 11 -> List.of("D", "E");
                    default -> List.of("A", "B", "C", "D", "E", "F", "G", "H");
                };

                for (String pos : seatPositions) {
                    Seat seat = new Seat();
                    seat.setPlane(plane);
                    seat.setRecommended(false);
                    seat.setRow(row);
                    seat.setSeat_column(pos);
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

        List<Seat> availableSeats = seatRepository.findByPlaneId(planeId).stream()
            .filter(Seat::getAvailable)
            .sorted(Comparator.comparingInt(Seat::getRow)
                .thenComparing(seat -> seat.getSeat_column().charAt(0)))
            .toList();

        List<Seat> recommendedSeats = findExactAdjacentSeats(availableSeats, seatCount);
        recommendedSeats.forEach(seat -> seat.setRecommended(true));

        seatRepository.saveAll(recommendedSeats); // Salvesta soovitused

        // Ainult seatCount arv soovitusi

        return availableSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(true)
                .recommended(seat.getRecommended())  // Ainult seatCount arv soovitusi
                .build())
            .toList();
    }

    private List<Seat> findExactAdjacentSeats(List<Seat> availableSeats, int seatCount) {
        Map<Integer, List<Seat>> rowSeatsMap = availableSeats.stream()
            .collect(Collectors.groupingBy(Seat::getRow));

        for (List<Seat> sameRowSeats : rowSeatsMap.values()) {
            sameRowSeats.sort(Comparator.comparing(seat -> seat.getSeat_column().charAt(0)));

            for (int i = 0; i <= sameRowSeats.size() - seatCount; i++) {
                List<Seat> group = sameRowSeats.subList(i, i + seatCount);
                if (isConsecutive(group)) {
                    return group;
                }
            }
        }
        return new ArrayList<>();
    }

    private boolean isConsecutive(List<Seat> seats) {
        for (int i = 1; i < seats.size(); i++) {
            if (seats.get(i).getSeat_column().charAt(0) - seats.get(i - 1).getSeat_column().charAt(0) != 1) {
                return false;
            }
        }
        return true;
    }

    public Map<String, List<SeatDTO>> getSeatsByFlight(Integer flightId, Integer planeId) {
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);
        List<SeatDTO> seatDTOS = allSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(seat.getAvailable())
                .recommended(seat.getRecommended())
                .build())
            .toList();
        Map<String, List<SeatDTO>> seatGroups = new HashMap<>();
        seatGroups.put("availableSeats", seatDTOS.stream().filter(seat -> seat.getAvailable() && !seat.getRecommended()).toList());
        seatGroups.put("bookedSeats", seatDTOS.stream().filter(seat -> !seat.getAvailable()).toList());
        seatGroups.put("recommendedSeats", seatDTOS.stream().filter(SeatDTO::getRecommended).toList());
        return seatGroups;
    }

    public List<SeatDTO> addFilters(Integer flightId, Integer seatCount, Integer planeId, List<Integer> filters) {
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);
        allSeats.forEach(seat -> seat.setRecommended(false));
        List<Seat> availableSeats = allSeats.stream()
            .filter(Seat::getAvailable)
            .toList();
        if (filters.contains(4) && seatCount > 1) {
            List<Seat> closestSeats = findExactAdjacentSeats(availableSeats, seatCount);
            if (!closestSeats.isEmpty()) {
                closestSeats.forEach(seat -> seat.setRecommended(true));
            }
        }
        List<Seat> filteredSeats = availableSeats.stream()
            .filter(seat -> !seat.getRecommended()) // Vältige juba soovitatud
            .filter(seat -> {
                boolean matchesAllFilters = true;
                if (filters.contains(1)) {
                    matchesAllFilters &= (seat.getSeat_column().equals("A") || seat.getSeat_column().equals("H"));
                }
                if (filters.contains(2)) {
                    List<String> exitRows = List.of("10", "4", "2", "6");
                    List<String> exitCols = List.of("A", "B", "G", "H");
                    matchesAllFilters &= (exitRows.contains(String.valueOf(seat.getRow())) &&
                        exitCols.contains(seat.getSeat_column()));
                }
                if (filters.contains(3)) {
                    List<String> exitRows = List.of("10", "4", "2", "6");
                    matchesAllFilters &= (exitRows.contains(String.valueOf(seat.getRow())) &&
                        (seat.getSeat_column().equals("A") || seat.getSeat_column().equals("H")));
                }

                return matchesAllFilters;
            })
            .toList();
        filteredSeats.forEach(seat -> seat.setRecommended(true));
        if (filteredSeats.size() < seatCount) {
            int remainingSeatsCount = seatCount - filteredSeats.size();
            List<Seat> remainingSeats = availableSeats.stream()
                .limit(remainingSeatsCount)
                .toList();
            remainingSeats.forEach(seat -> seat.setRecommended(true));
            List<Seat> updatedFilteredSeats = new ArrayList<>(filteredSeats);
            updatedFilteredSeats.addAll(remainingSeats);
            filteredSeats = updatedFilteredSeats;
        }
        seatRepository.saveAll(allSeats);
        return filteredSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(planeId)
                .seat_column(seat.getSeat_column())
                .recommended(true)
                .available(true)
                .build())
            .limit(seatCount)
            .toList();
    }
}
