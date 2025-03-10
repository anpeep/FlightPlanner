package com.example.test.repository;

import com.example.test.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByPlaneIdAndAvailable(Integer planeId, Boolean available);

    List<Seat> findByFlightId(Integer flightId);
}
