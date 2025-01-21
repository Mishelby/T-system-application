package org.example.logisticapplication.web;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalHandlerExceptions {

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorMessageResponse> illegalArgumentException(
            Exception ex
    ) {

        var detailedMessage = (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException)
                ? constructMethodArgumentNotValidMessage(methodArgumentNotValidException)
                : ex.getMessage();

        var response = new ErrorMessageResponse(
                "Illegal argument exception!",
                detailedMessage,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private static String constructMethodArgumentNotValidMessage(
            MethodArgumentNotValidException ex
    ) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> entityNotFoundException(
            EntityNotFoundException ex
    ) {
        var response = new ErrorMessageResponse(
                "Entity not found!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<ErrorMessageResponse> entityNotFoundException(
            NoSuchElementException ex
    ) {
        var response = new ErrorMessageResponse(
                "No such element!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ErrorMessageResponse> entityNotFoundException(
            NoResourceFoundException ex
    ) {
        var response = new ErrorMessageResponse(
                "No resource found!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageResponse> entityAlreadyExistsException(
            EntityAlreadyExistsException ex
    ) {
        var response = new ErrorMessageResponse(
                "Entity already exists!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
