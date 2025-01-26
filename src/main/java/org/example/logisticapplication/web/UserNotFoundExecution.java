package org.example.logisticapplication.web;

public class UserNotFoundExecution extends RuntimeException {
    public UserNotFoundExecution(String message) {
        super(message);
    }
    public UserNotFoundExecution(String message, Throwable cause) {
        super(message, cause);
    }
}
