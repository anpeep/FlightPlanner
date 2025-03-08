package com.example.test.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
public class Flight {
    @Id
    private Integer id;
    private String departureCity;
    private String arrivalCity;
    private Instant departOn;
    private Instant arriveOn;
    @ManyToOne
    @JoinColumn(name = "planeId")
    private Plane plane;

}
