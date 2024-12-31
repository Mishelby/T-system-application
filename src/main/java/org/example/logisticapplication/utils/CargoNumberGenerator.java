package org.example.logisticapplication.utils;

import java.util.concurrent.atomic.AtomicInteger;


public class CargoNumberGenerator {
    private static final AtomicInteger CARGO_ID_GENERATOR = new AtomicInteger();
    private static final String DEFAULT_NAME = "Cargo";

    public static String generateNumber() {
        return DEFAULT_NAME.concat(" - " + CARGO_ID_GENERATOR.incrementAndGet());
    }
}
