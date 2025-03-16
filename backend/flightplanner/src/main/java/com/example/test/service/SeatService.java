package com.example.test.service;

import com.example.test.dto.SeatDTO;
import com.example.test.mapping.SeatMapper;
import com.example.test.model.Plane;
import com.example.test.model.Seat;
import com.example.test.repository.PlaneRepository;
import com.example.test.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final PlaneRepository planeRepository;
    private final SeatMapper seatMapper;
    private final Random random = new Random();

    public Map<String, List<SeatDTO>> getSeatsByFlight(Integer flightId, Integer planeId) {
        if (seatRepository.countByPlaneId(planeId) == 0) {
            generateBookedSeats(planeId, flightId);
        }
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);
        List<SeatDTO> seatDTOS = allSeats.stream()
            .map(seatMapper::toDTO)
            .toList();

        Map<String, List<SeatDTO>> seatGroups = new HashMap<>();
        seatGroups.put("availableSeats", seatDTOS.stream().filter(seat -> seat.getAvailable() && !seat.getRecommended()).toList());
        seatGroups.put("bookedSeats", seatDTOS.stream().filter(seat -> !seat.getAvailable()).toList());
        seatGroups.put("recommendedSeats", seatDTOS.stream().filter(SeatDTO::getRecommended).toList());
        return seatGroups;
    }

    public List<SeatDTO> generateBookedSeats(Integer planeId, Integer flightId) {
        Plane plane = planeRepository.findById(planeId)
            .orElseThrow(() -> new IllegalArgumentException("Plane not found with ID: " + planeId));
        if (seatRepository.countByPlaneId(planeId) == 0) {
            List<Seat> newSeats = new ArrayList<>();
            for (int row = 1; row <= 11; row++) {
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
            int totalSeats = 72;
            int bookedSeatsCount = random.nextInt(totalSeats - 1);
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

        List<Seat> recommendedSeats = findExactAdjacentSeats(availableSeats, 1);
        recommendedSeats.forEach(seat -> seat.setRecommended(true));
        seatRepository.saveAll(recommendedSeats);
        List<SeatDTO> seatDTOS = availableSeats.stream()
            .map(seatMapper::toDTO)
            .toList();
        seatDTOS.forEach(seat -> seat.setPlaneId(planeId));

        return seatDTOS;
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


    public List<SeatDTO> addFilters(Integer flightId, Integer seatCount, Integer planeId, List<Integer> filters) {
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);
        List<Seat> availableSeats = allSeats.stream().filter(Seat::getAvailable).toList();

        List<Seat> adjacentSeats;
        if (filters.contains(4) && seatCount > 1) {
            adjacentSeats = findExactAdjacentSeats(availableSeats, seatCount);
        } else {
            adjacentSeats = new ArrayList<>();
        }

        List<Seat> filteredSeats = availableSeats.stream()
            .filter(seat -> {
                boolean matchesAllFilters = true;

                if (filters.contains(1)) {
                    matchesAllFilters &= (seat.getSeat_column().equals("A") || seat.getSeat_column().equals("H"));
                }
                if (filters.contains(2)) {
                    List<String> exitCols1 = List.of("A", "B", "G", "H");
                    List<String> exitRow1 = List.of("C", "F");
                    List<String> exitRows2 = List.of("4", "6", "10");
                    List<String> exitCols2 = List.of("A", "B", "C", "F", "G", "H");

                    matchesAllFilters &= (
                        (seat.getRow() == 2 && exitCols1.contains(seat.getSeat_column())) ||
                            (seat.getRow() == 1 && exitRow1.contains(seat.getSeat_column())) ||
                            (exitRows2.contains(String.valueOf(seat.getRow())) && exitCols2.contains(seat.getSeat_column())) ||
                            (seat.getRow() == 11 || seat.getRow() == 5)
                    );
                }
                if (filters.contains(3)) {
                    matchesAllFilters &= (
                        (seat.getRow() == 2 && List.of("A", "B", "G", "H").contains(seat.getSeat_column())) ||
                            (seat.getRow() == 6 && List.of("A", "B", "C", "F", "G", "H").contains(seat.getSeat_column()))
                    );
                }
                if (filters.contains(4) && seatCount > 1) {
                    matchesAllFilters &= !adjacentSeats.isEmpty() && adjacentSeats.contains(seat);
                }

                return matchesAllFilters;
            })
            .toList();
        List<Seat> recommendedSeats = filteredSeats.stream().limit(seatCount).toList();
        recommendedSeats.forEach(seat -> seat.setAvailable(true));
        recommendedSeats.forEach(seat -> seat.setRecommended(true));
        seatRepository.saveAll(recommendedSeats);
        return recommendedSeats.stream()
            .map(seatMapper::toDTO)
            .toList();
    }
}
