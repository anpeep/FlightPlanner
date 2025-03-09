package com.example.test.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatDTO {
    private String id;
    private Integer planeId;
    private Boolean available;
}
