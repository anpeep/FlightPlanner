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
                List<String> seatPositions;

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

            for (int i = 0; i < bookedSeatsCount - 2; i++) {
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

    public Map<String, List<SeatDTO>> getSeatsByFlight(Integer flightId, Integer planeId) {
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);
        List<SeatDTO> seatDTOS = allSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(seat.getAvailable())
                .recommended(isRecommendedSeat(seat, flightId))
                .build())
            .toList();
        Map<String, List<SeatDTO>> seatGroups = new HashMap<>();
        seatGroups.put("availableSeats", seatDTOS.stream().filter(seat -> seat.getAvailable() && !seat.getRecommended()).toList());
        seatGroups.put("bookedSeats", seatDTOS.stream().filter(seat -> !seat.getAvailable() && !seat.getRecommended()).toList());
        seatGroups.put("recommendedSeats", seatDTOS.stream().filter(SeatDTO::getRecommended).toList());  // Täiendav kontroll siin!
        return seatGroups;
    }
    private boolean isRecommendedSeat(Seat seat, Integer flightId) {
        return ticketRepository.existsBySeatIdAndFlightId(seat.getId(), flightId);
    }

    public List<SeatDTO> addFilters(Integer flightId, Integer seatCount, Integer planeId, List<Integer> filters) {
        // Leia kõik toolid selle lennuki jaoks
        List<Seat> allSeats = seatRepository.findByPlaneId(planeId);

        // 1️⃣ EEMALDA KÕIK VARASEMAD SOOVITUSED
        allSeats.forEach(seat -> seat.setRecommended(false));

        // 2️⃣ FILTREERI VABAD TOOLID
        List<Seat> availableSeats = allSeats.stream()
            .filter(Seat::getAvailable)  // Ainult saadaval olevad toolid
            .toList();

        // 3️⃣ RAKENDA KÕIK FILTRID DÜNAAMILISELT
        List<Seat> filteredSeats = availableSeats.stream()
            .filter(seat -> {
                boolean matchesAllFilters = true; // Tool peab vastama kõigile valitud filtritele

                if (filters.contains(1)) { // Kas tool on akna ääres?
                    matchesAllFilters &= (seat.getSeat_column().equals("A") || seat.getSeat_column().equals("H"));
                }
                if (filters.contains(2)) { // Kas tool on väljapääsu rea juures?
                    List<String> exitRows = List.of("10", "4", "2", "6"); // Väljapääsu read
                    List<String> exitCols = List.of("A", "B", "G", "H"); // Väljapääsu kohad
                    matchesAllFilters &= (exitRows.contains(String.valueOf(seat.getRow())) &&
                        exitCols.contains(seat.getSeat_column()));
                }
                if (filters.contains(3)) { // Kas tool on väljapääsu juures ja akna ääres?
                    List<String> exitRows = List.of("10", "4", "2", "6");
                    matchesAllFilters &= (exitRows.contains(String.valueOf(seat.getRow())) &&
                        (seat.getSeat_column().equals("A") || seat.getSeat_column().equals("H")));
                }
                if (filters.contains(4) && availableSeats.size() > 1) {
                    matchesAllFilters &= isNear(seat, availableSeats, seatCount);
                }
                return matchesAllFilters;
            })
            .toList();

        // Kui leidub toolid, mis vastavad kõigile filtritele, märgime need soovitatuks
        if (!filteredSeats.isEmpty()) {
            filteredSeats.forEach(seat -> seat.setRecommended(true));
        }

        // Kui ei ole piisavalt filtreeritud kohti, siis täiendame suvaliste toolidega
// 4️⃣ Kui ei ole piisavalt filtreeritud kohti, siis täiendame suvaliste toolidega
        if (filteredSeats.size() < seatCount) {
            int remainingSeatsCount = seatCount - filteredSeats.size();

            // Vali suvaliselt vabad toolid
            List<Seat> remainingSeats = availableSeats.stream()
                .limit(remainingSeatsCount)
                .toList();
            // Märgi need toolid soovitatuks
            remainingSeats.forEach(seat -> seat.setRecommended(true));

            // Loo uus list, mis sisaldab nii filtreeritud kui ka suvalisi kohti
            List<Seat> updatedFilteredSeats = new ArrayList<>(filteredSeats);
            updatedFilteredSeats.addAll(remainingSeats); // Lisa suvalised toolid

            // Kasuta uut updatedFilteredSeats listi edasistes toimingutes
            filteredSeats = updatedFilteredSeats;
        }


        // 4️⃣ SALVESTA UUENDATUD TOOLID
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
            .limit(seatCount) // Võtame ainult seatCount arv toole
            .toList();
    }

    private boolean isNear(Seat currentSeat, List<Seat> availableSeats, Integer seatCount) {
        // Kui seatCount on suurem kui 1, proovime leida järjestatud kohti
        if (seatCount > 1) {
            // Kogume kõik toolid, mis on samas reas ja järjestatud
            List<Seat> nearbySeats = availableSeats.stream()
                .filter(seat -> seat.getRow().equals(currentSeat.getRow()))
                .sorted(Comparator.comparing(seat -> seat.getSeat_column().charAt(0)))  // Sorteerime toolid samas reas
                .toList();

            // Leiame järjestatud toolid, mis on üksteise kõrval
            for (int i = 0; i < nearbySeats.size() - seatCount + 1; i++) {
                List<Seat> possibleSeats = nearbySeats.subList(i, i + seatCount);  // Kontrollime järgmise seatCount arvu järjestatud kohti
                boolean areSeatsAdjacent = true;

                // Kontrollime, kas need toolid on üksteise kõrval
                for (int j = 0; j < possibleSeats.size() - 1; j++) {
                    if (Math.abs(possibleSeats.get(j).getSeat_column().charAt(0) - possibleSeats.get(j + 1).getSeat_column().charAt(0)) != 1) {
                        areSeatsAdjacent = false;
                        break;
                    }
                }

                if (areSeatsAdjacent) {
                    // Kui need toolid on üksteise kõrval, siis tagastame tõene
                    return true;
                }
            }
        }
        return false;
    }
}
