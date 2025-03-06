package com.example.test.repository;

import com.example.test.model.Passenger;
import com.example.test.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByClient(Passenger passenger);


}
