package com.example.test.controller;

import com.example.test.service.StartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class StartController {
    private final StartService startService;

    @PostMapping("/start")
    public ResponseEntity<Integer> start() {
        Integer planeId = startService.generateRandomFlights();
        return ResponseEntity.ok(planeId);
    }
}
