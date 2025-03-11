package com.example.test.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class Seating {
    private SeatFilter filter;
    private int row;
    private String column;
    private String type;
}
