package com.example.test.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
public class Plane {
    @Id
    private Long id;
    private String airport;
    private int departureTime;
    private int arrivalTime;
}
