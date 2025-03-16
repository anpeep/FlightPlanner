package com.example.test.mapping;

import com.example.test.dto.SeatDTO;
import com.example.test.model.Seat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    SeatDTO toDTO(Seat seat);
}
