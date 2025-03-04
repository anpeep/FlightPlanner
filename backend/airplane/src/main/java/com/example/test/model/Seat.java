package com.example.test.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Seat {
    @Id
    private Long id;
    private float price;
    private boolean available;
    private boolean windowSeat;
    private boolean legRoom;
    private boolean recline;
    private boolean wheelChair;
    private boolean nearBoarding;
}
