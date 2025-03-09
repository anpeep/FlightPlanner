package com.example.test.mapping;

import com.example.test.dto.FlightDTO;
import com.example.test.model.Flight;
import org.mapstruct.Mapper;

@Mapper
public interface FlightMapper {
    FlightDTO toDTO(Flight flight);
    Flight toEntity(FlightDTO flightDTO);
}

