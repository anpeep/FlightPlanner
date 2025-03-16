package com.example.test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "plane")

public class Plane {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @OneToMany(mappedBy = "plane", cascade = CascadeType.ALL)
    private List<Flight> flights;
}
