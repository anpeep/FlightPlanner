package com.example.test.mapping;

import org.mapstruct.Mapper;
import com.example.test.dto.FlightDTO;
import com.example.test.model.Flight;

@Mapper(componentModel = "spring")  // Make sure this is present
public interface FlightMapper {

    FlightDTO toDTO(Flight flight);  // Map Flight entity to FlightDTO

    Flight toEntity(FlightDTO flightDTO);  // Map FlightDTO to Flight entity
}
