package com.example.test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Entity
@Getter
@Table(name = "ticket")
public class Ticket {
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "seat_string")
    private Seat seat;
    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
    private Float price;

}
