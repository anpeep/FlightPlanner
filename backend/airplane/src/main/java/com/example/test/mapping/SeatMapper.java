package com.example.test.mapping;

import com.example.test.dto.SeatDTO;
import com.example.test.model.Seat;
import org.mapstruct.Mapper;

@Mapper
public interface SeatMapper {
    SeatDTO toDTO(Seat seat);
    Seat toEntity(SeatDTO seatDTO);
}
