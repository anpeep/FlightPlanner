package com.example.test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "plane_id")
    private Plane plane;
    private String departureCity;
    private String arrivalCity;
    private Instant departOn;
    private Instant arriveOn;
    private Float price;
}
