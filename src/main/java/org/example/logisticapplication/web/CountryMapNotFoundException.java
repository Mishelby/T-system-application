package org.example.logisticapplication.web;

public class CountryMapNotFoundException extends RuntimeException {
    public CountryMapNotFoundException() {
    }

    public CountryMapNotFoundException(String message) {
        super(message);
    }

    public CountryMapNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
