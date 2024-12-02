package org.example.logisticapplication.web;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorMessageResponse> illegalArgumentException(
            Exception ex
    ) {
        ErrorMessageResponse response = new ErrorMessageResponse(
                "Illegal argument exception!",
                ex.getMessage(),
                LocalDateTime.now()
        );


        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> entityNotFoundException(
            EntityNotFoundException ex
    ){
        ErrorMessageResponse response = new ErrorMessageResponse(
                "Entity not found!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<ErrorMessageResponse> entityNotFoundException(
            NoSuchElementException ex
    ){
        ErrorMessageResponse response = new ErrorMessageResponse(
                "Entity not found!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ErrorMessageResponse> entityNotFoundException(
            NoResourceFoundException ex
    ){
        ErrorMessageResponse response = new ErrorMessageResponse(
                "Entity not found!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
