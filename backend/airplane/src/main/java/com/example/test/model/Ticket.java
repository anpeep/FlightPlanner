package com.example.test.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Entity
@Getter
public class Ticket {
    @Id
    private Long id;
    private float price;
    private boolean available;
    private boolean windowSeat;
    private boolean legRoom;
    private boolean recline;
    private boolean wheelChair;
    private boolean nearBoarding;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
