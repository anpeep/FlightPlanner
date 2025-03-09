package com.example.test.service;
import com.example.test.assets.Seating;
import com.example.test.dto.SeatDTO;
import com.example.test.mapping.SeatMapper;
import com.example.test.model.Plane;
import com.example.test.model.Seat;
import com.example.test.assets.SeatFilter;
import com.example.test.repository.PlaneRepository;
import com.example.test.repository.SeatRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final SeatMapper seatMapper;
    private final Map<String, Seating> seatFilter = new HashMap<>();
    private final Random random = new Random();

    /**
     * When flight is chosen, book random seats
     */
    @PostConstruct
    public void init() {
        parseJson();
        if (seatRepository.count() == 0) {
            List<Plane> planes = planeRepository.findAll();
            List<SeatDTO> seats = planes.stream()
                .flatMap(plane -> seatFilter.values().stream()
                    .map(seatingData -> {
                        boolean isAvailable = random.nextBoolean();
                        return SeatDTO.builder()
                            .planeId(plane.getId())
                            .available(isAvailable)
                            .build();
                    }))
                .toList();
            List<Seat> seatEntities = seats.stream()
                .map(seatMapper::toEntity)
                .collect(Collectors.toList());

            seatRepository.saveAll(seatEntities);
        }
    }

    /**
     * Get available seats
     */
    public List<SeatDTO> findAvailableSeats(Integer planeId, int seatCount, SeatFilter selectedFilter) {
        List<Seat> availableSeats = seatRepository.findByPlaneIdAndAvailable(planeId, true);
        List<SeatDTO> availableSeatDTOs = availableSeats.stream()
            .filter(seat -> seatFilter.containsKey(seat.getId()))
            .map(seat -> {
                Seating seatingData = seatFilter.get(seat.getId());
                if (seatingData != null && seatingData.getFilter().equals(selectedFilter)) {
                    return seatMapper.toDTO(seat);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (availableSeatDTOs.size() < seatCount) {
            // Kui ei ole piisavalt kohti, proovime leida asenduskohad
            availableSeatDTOs.addAll(findAlternativeSeats(availableSeats, seatCount - availableSeatDTOs.size(), selectedFilter));
        }

        return findNearbySeats(availableSeatDTOs, seatCount);
    }
    private List<SeatDTO> findAlternativeSeats(List<Seat> availableSeats, int remainingSeats, SeatFilter selectedFilter) {
        // Otsime alternatiivseid kohti, mis vastavad valitud filtrile ja on saadaval
        return availableSeats.stream()
            .filter(seat -> seatFilter.containsKey(seat.getId())) // Veendume, et see koht on seatFilteris
            .map(seat -> {
                Seating seatingData = seatFilter.get(seat.getId());
                if (seatingData != null && seatingData.getFilter().equals(selectedFilter) && seat.getAvailable()) {
                    return seatMapper.toDTO(seat);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .limit(remainingSeats) // Vaatame, kui palju alternatiivseid kohti on vaja
            .collect(Collectors.toList());
    }
    /**
     * If multiple tickets, but no seats available next to each other or at the same row, find near each other
     */
    private List<SeatDTO> findNearbySeats(List<SeatDTO> availableSeats, int seatCount) {
        List<SeatDTO> result = new ArrayList<>();
        List<SeatDTO> tempSeats = new ArrayList<>();

        for (SeatDTO seat : availableSeats) {
            if (tempSeats.isEmpty() || isNextTo(tempSeats.getLast(), seat)) {
                tempSeats.add(seat);
            } else {
                if (tempSeats.size() >= seatCount) {
                    result.addAll(tempSeats.subList(0, seatCount));
                    break;
                }
                tempSeats.clear();
                tempSeats.add(seat);
            }
        }
        if (tempSeats.size() >= seatCount) {
            result.addAll(tempSeats.subList(0, seatCount));
        }
        if (result.size() < seatCount) {
            result.addAll(findSeatsInSameRow(availableSeats, seatCount - result.size()));
        }

        return result;
    }

    /**
     * If multiple tickets, find seats available next to each other
     */
    private boolean isNextTo(SeatDTO seat1, SeatDTO seat2) {
        int row1 = Integer.parseInt(seat1.getId().substring(0, seat1.getId().length() - 1));
        char col1 = seat1.getId().charAt(seat1.getId().length() - 1);
        int row2 = Integer.parseInt(seat2.getId().substring(0, seat2.getId().length() - 1));
        char col2 = seat2.getId().charAt(seat2.getId().length() - 1);
        return row1 == row2 && col1 == col2 - 1;
    }
    /**
     * If multiple tickets, but no seats available next to each other, find at the same row
     */
    private List<SeatDTO> findSeatsInSameRow(List<SeatDTO> availableSeats, int remainingSeats) {
        Map<Integer, List<SeatDTO>> seatsByRow = availableSeats.stream()
            .collect(Collectors.groupingBy(seat -> Integer.parseInt(seat.getId().substring(0, seat.getId().length() - 1))));
        List<SeatDTO> result = new ArrayList<>();
        for (List<SeatDTO> rowSeats : seatsByRow.values()) {
            if (rowSeats.size() >= remainingSeats) {
                result.addAll(rowSeats.subList(0, remainingSeats));
                break;
            }
        }

        return result;
    }
    public void bookSeats(List<Integer> seatId) {
        List<Seat> seatsToBook = seatRepository.findAllById(seatId);
        seatsToBook.forEach(seat -> seat.setAvailable(false));
        seatRepository.saveAll(seatsToBook);
    }

    private void parseJson() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Seats.json");
            if (inputStream == null) {
                throw new RuntimeException("Seats.json not found!");
            }
            String jsonData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            List<Seating> seatFilters = objectMapper.readValue(jsonData, new TypeReference<>() {
            });
            seatFilters.forEach(seat -> seatFilter.put(seat.getRow() + seat.getColumn(), seat));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Seats.json", e);
        }
    }

}
