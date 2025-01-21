package org.example.logisticapplication.web;

public class TruckDeletionException extends RuntimeException {
    public TruckDeletionException(String message) {
        super(message);
    }

    public TruckDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
