package com.example.test.mapping;

import com.example.test.dto.PlaneDTO;
import com.example.test.model.Plane;
import org.mapstruct.Mapper;

@Mapper

public interface PlaneMapper {
    PlaneDTO toDTO(Plane plane);

}
