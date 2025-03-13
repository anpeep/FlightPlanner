package com.example.test.mapping;

import com.example.test.dto.FlightDTO;
import com.example.test.model.Flight;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")  // Make sure this is present
public interface FlightMapper {

    FlightDTO toDTO(Flight flight);  // Map Flight entity to FlightDTO

    Flight toEntity(FlightDTO flightDTO);  // Map FlightDTO to Flight entity
}
