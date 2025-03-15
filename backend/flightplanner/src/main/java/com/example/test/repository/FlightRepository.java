package com.example.test.repository;

import com.example.test.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

    List<Flight> findByPlaneId(Integer planeId);
}
