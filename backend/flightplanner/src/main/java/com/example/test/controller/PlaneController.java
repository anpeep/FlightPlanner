package com.example.test.controller;

import com.example.test.dto.PlaneDTO;
import com.example.test.service.PlaneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RequestMapping("plane/")
@RestController
public class PlaneController {

    private final PlaneService planeService;

    @RequestMapping("getFlightsForPlane")
    public ResponseEntity<PlaneDTO> getFlightsForPlane(@RequestParam Integer planeId) {
        try {
            PlaneDTO planeDTO = planeService.getFlightsForPlane(planeId);  // Fetch flights associated with the plane
            return new ResponseEntity<>(planeDTO, HttpStatus.OK);  // Return 200 OK if found
        } catch (NoSuchElementException ex) {
            // Return 404 Not Found if plane is not found
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
