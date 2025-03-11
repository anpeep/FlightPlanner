package com.example.test.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatDTO {
    private Integer id;
    private Integer planeId;
    private Boolean available;
    private Integer row;
    private String seat_column;
}
