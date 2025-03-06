package com.example.test.mapping;

import com.example.test.dto.PassengerDTO;
import com.example.test.model.Passenger;
import org.mapstruct.Mapper;

@Mapper
public interface PassengerMapper {
    PassengerDTO toDTO(Passenger passenger);
    Passenger toEntity(PassengerDTO dto);
}
