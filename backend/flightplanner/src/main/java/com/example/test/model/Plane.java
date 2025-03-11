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

    @GeneratedValue(strategy = GenerationType.IDENTITY) // Make sure this is present
    @Id
    private Integer id;
    @OneToMany(mappedBy = "plane")  // Use 'mappedBy' to indicate the owning side of the relationship
    private List<Flight> flights;
}
