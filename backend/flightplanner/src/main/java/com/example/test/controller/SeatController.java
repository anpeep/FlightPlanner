package com.example.test.controller;

import com.example.test.dto.SeatDTO;
import com.example.test.model.Seat;
import com.example.test.repository.SeatRepository;
import com.example.test.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

        // 5️⃣ KONVERTEERI DTO-DEKS JA TAGASTA AINULT seatCount ARV SOOVITATUD TOOLE
        List<SeatDTO> seatDTOs = filteredSeats.stream()
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

        return ResponseEntity.ok(seatDTOs);
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



}
