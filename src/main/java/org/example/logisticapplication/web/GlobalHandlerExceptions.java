package org.example.logisticapplication.web;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
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

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> accessDeniedException(
            AccessDeniedException ex
    ){
        var response = new ErrorMessageResponse(
                "У вас нет прав доступа!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> badCredentialsException(
            BadCredentialsException ex
    ) {
        var response = new ErrorMessageResponse(
                "Bad credentials!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
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

    @ExceptionHandler(value = UserNotFoundExecution.class)
    public ResponseEntity<String> userNotFoundExecution(
            UserNotFoundExecution ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value = IncorrectUserDataForLogin.class)
    public ResponseEntity<ErrorMessageResponse> incorrectUserDataForLogin(
            IncorrectUserDataForLogin ex
    ) {
        var response = new ErrorMessageResponse(
                "Incorrect user data for login!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
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

    private static String constructMethodArgumentNotValidMessage(
            MethodArgumentNotValidException ex
    ) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }
}
