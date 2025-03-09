package com.example.test.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatDTO {
    private Integer id;
    private Integer planeId;
    private String name;
    private String type;
    private boolean is_more_leg_room;
}
