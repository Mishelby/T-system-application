package org.example.logisticapplication.web;

public class TruckAlreadyExists extends RuntimeException {
    public TruckAlreadyExists(String message) {
        super(message);
    }

    public TruckAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    public TruckAlreadyExists() {
    }
}
