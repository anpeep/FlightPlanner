package com.example.test.controller;

import com.example.test.assets.SeatFilter;
import com.example.test.dto.SeatDTO;
import com.example.test.model.Seat;
import com.example.test.repository.SeatRepository;
import com.example.test.service.SeatLoader;
import com.example.test.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/api/seats")
@RestController
public class SeatController {
    private final SeatService seatService;
    private final SeatRepository seatRepository;

    @PostMapping("/getSeats")
    public ResponseEntity<List<SeatDTO>> getSeats(
        @RequestParam Integer seatCount,
        @RequestParam Integer planeId,
        @RequestParam Integer flightId) {

        List<SeatDTO> seats = seatService.generateAndRecommendSeats(seatCount, planeId, flightId);
        return ResponseEntity.ok(seats);
    }
    @GetMapping("/getSeatsByFlight")
    public ResponseEntity<Map<String, List<SeatDTO>>> getSeatsByFlight(
        @RequestParam Integer flightId,
        @RequestParam Integer planeId) {

        // Kõik toolid
        List<SeatDTO> allSeats = seatService.getSeatsByFlight(flightId, planeId);

        Map<String, List<SeatDTO>> seatGroups = new HashMap<>();
        seatGroups.put("availableSeats", allSeats.stream().filter(seat -> seat.getAvailable() && !seat.getRecommended()).toList());
        seatGroups.put("bookedSeats", allSeats.stream().filter(seat -> !seat.getAvailable() && !seat.getRecommended()).toList());
        seatGroups.put("recommendedSeats", allSeats.stream().filter(SeatDTO::getRecommended).toList());  // Täiendav kontroll siin!

        return ResponseEntity.ok(seatGroups);
    }
    @PostMapping("/addFilters")
    public ResponseEntity<List<SeatDTO>> setRecommendedSeats(
        @RequestParam Integer flightId,
        @RequestParam Integer seatCount,
        @RequestParam Integer planeId,
        @RequestBody List<Integer> filters) {

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
                if (filters.contains(4)) {
                    matchesAllFilters &= isNear(seat, availableSeats);
                }
                return matchesAllFilters;
            })
            .toList();

        // Kui leidub toolid, mis vastavad kõigile filtritele, märgime need soovitatuks
        if (!filteredSeats.isEmpty()) {
            filteredSeats.forEach(seat -> seat.setRecommended(true));
        }

        // 4️⃣ SALVESTA UUENDATUD TOOLID
        seatRepository.saveAll(allSeats);

        // 5️⃣ KONVERTEERI DTO-DEKS JA TAGASTA AINULT seatCount ARV SOOVITATUD TOOLE
        List<SeatDTO> seatDTOs = filteredSeats.stream()
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(planeId)
                .seat_column(seat.getSeat_column())
                .recommended(true)
                .available(false)
                .build())
            .limit(seatCount) // Võtame ainult seatCount arv toole
            .toList();

        return ResponseEntity.ok(seatDTOs);
    }



    @PostMapping("/recommendSeatsWithFilters")
    public ResponseEntity<List<SeatDTO>> recommendSeatsWithFilters(
        @RequestParam Integer flightId,
        @RequestParam Integer planeId,
        @RequestBody List<Integer> filters) {

        // Eemaldame kõik senised soovitatud toolid (kui neid on)
        seatRepository.findByPlaneId(planeId).forEach(seat -> seat.setRecommended(false));

        // Filtreeritud soovitatavad toolid
        List<Seat> filteredSeats = seatService.applyFiltersAndRecommendSeats(planeId, flightId, filters);

        // Salvesta uuesti toolid, millele on soovitused määratud
        seatRepository.saveAll(filteredSeats);

        // Muuda toolid DTO-deks, et neid saaks tagastada
        List<SeatDTO> seatDTOs = filteredSeats.stream().filter(seat -> seat.getAvailable() && !seat.getRecommended())
            .map(seat -> SeatDTO.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .planeId(seat.getPlane().getId())
                .seat_column(seat.getSeat_column())
                .available(seat.getAvailable())
                .recommended(seat.getRecommended())  // Siin määrame uue soovituse väärtuse
                .build())
            .toList();

        return ResponseEntity.ok(seatDTOs);
    }
    private boolean isNear(Seat currentSeat, List<Seat> availableSeats) {
        // Kogume kõik toolid, mis on samas reas või järgmistes ridades
        List<Seat> nearbySeats = availableSeats.stream()
            .filter(seat ->
                seat.getRow().equals(currentSeat.getRow()) ||
                    Math.abs((seat.getRow()) - (currentSeat.getRow())) == 1) // Samas reas või järgmises reas
            .toList();

        // Kontrollige, kas vähemalt üks tool on järgmises reas ja see on samasugune või erinev positsioonil
        return nearbySeats.stream()
            .anyMatch(seat ->
                Math.abs(seat.getSeat_column().charAt(0) - currentSeat.getSeat_column().charAt(0)) <= 1 // Toolid on üksteisele lähedal
            );
    }


}
