package com.example.test.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlaneDTO {
    private List<FlightDTO> flights;
    private Integer id;
}
