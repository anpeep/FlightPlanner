package com.example.test.service;
import com.example.test.dto.SeatDTO;
import com.example.test.mapping.SeatMapper;
import com.example.test.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeatService {
    private  SeatRepository seatRepository;
    private  SeatMapper seatMapper;
    public SeatDTO createSeat(SeatDTO seatDTO) {
        return seatDTO;
    }

    public Optional<SeatDTO> getSeatById(Integer id) {
        return seatRepository.findById(id).map(seatMapper::toDTO);
    }
}
