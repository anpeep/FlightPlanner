package com.example.test.service;
import com.example.test.dto.PlaneDTO;
import com.example.test.mapping.PlaneMapper;
import com.example.test.repository.PlaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PlaneService {
    private final PlaneRepository planeRepository;
    private final PlaneMapper planeMapper;
    public PlaneDTO getFittingPlane(PlaneDTO planeDTO) {
        //TODO: over your
        return planeDTO;
    }
}
