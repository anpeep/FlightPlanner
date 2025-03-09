package com.example.test.mapping;

import com.example.test.dto.FlightDTO;
import com.example.test.dto.PlaneDTO;
import com.example.test.model.Flight;
import com.example.test.model.Plane;
import org.mapstruct.Mapper;
@Mapper

public interface PlaneMapping {
    PlaneDTO toDTO(Plane plane);
    Plane toEntity(PlaneDTO planeDTO);

}
