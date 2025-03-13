package com.example.test.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Seating {
    private List<SeatFilter> filter;
    private int row;
    private String column;
    private String type;
}
