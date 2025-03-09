package com.example.test.service;

import com.example.test.dto.PlaneDTO;
import com.example.test.mapping.PlaneMapper;
import com.example.test.model.Plane;
import com.example.test.repository.PlaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaneService {
    private final PlaneRepository planeRepository;
    private final PlaneMapper planeMapper;
    public List<PlaneDTO> getAllPlanes() {
        return planeRepository.findAll().stream().map(planeMapper::toDTO).collect(Collectors.toList());
    }
}
