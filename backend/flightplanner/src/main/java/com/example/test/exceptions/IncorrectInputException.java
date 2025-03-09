package com.example.test.exceptions;

public class IncorrectInputException  extends RuntimeException {
    public IncorrectInputException(String message) {
        super(message);
    }
}
