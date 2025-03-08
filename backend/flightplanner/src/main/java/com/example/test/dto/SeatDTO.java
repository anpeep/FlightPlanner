package com.example.test.dto;
import com.example.test.model.Plane;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
