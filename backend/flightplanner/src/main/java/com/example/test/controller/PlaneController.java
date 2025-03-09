package com.example.test.controller;

import com.example.test.dto.PlaneDTO;
import com.example.test.service.PlaneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("plane/")
@RestController
public class PlaneController {
    private final PlaneService planeService;

    @GetMapping
    public ResponseEntity<List<PlaneDTO>> getAllPlanes() {
        List<PlaneDTO> planes = planeService.getAllPlanes();
        return new ResponseEntity<>(planes, HttpStatus.OK);
    }
}
