//package com.example.test.exceptions.handler;
//
//
//import com.example.test.exceptions.IncorrectInputException;
//import com.example.test.exceptions.NotFoundException;
//import com.example.test.exceptions.response.ExceptionResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.method.annotation.HandlerMethodValidationException;
//
//@ControllerAdvice
//
//public class ErrorHandler {
//
//    /**
//     * Handles uncaught exceptions.
//     */
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
//        return new ResponseEntity<>(new ExceptionResponse("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    /**
//     * Handles 404 (not found) type exceptions.
//     */
//    @ExceptionHandler(NotFoundException.class)
//    public ResponseEntity<ExceptionResponse> handleNotFoundException(Exception ex) {
//        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
//    }
//
//
//    @ExceptionHandler(IncorrectInputException.class)
//    public ResponseEntity<ExceptionResponse> handleIncorrectInputException(Exception ex) {
//        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
//        String message = ex.getBindingResult().getFieldErrors().stream()
//                .map(error -> error.getField() + ": " + error.getDefaultMessage())
//                .findFirst().orElse("Validation failed");
//        return new ResponseEntity<>(new ExceptionResponse(message), HttpStatus.BAD_REQUEST);
//    }
//
//
//}
