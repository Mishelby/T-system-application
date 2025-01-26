package org.example.logisticapplication.web;

public class IncorrectUserDataForLogin extends RuntimeException {
    public IncorrectUserDataForLogin(String message) {
        super(message);
    }
    public IncorrectUserDataForLogin(String message, Throwable cause) {
        super(message, cause);
    }
}
