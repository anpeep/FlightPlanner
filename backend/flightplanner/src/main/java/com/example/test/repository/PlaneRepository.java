package com.example.test.repository;

import com.example.test.model.Plane;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaneRepository extends JpaRepository<Plane, Integer> {}
