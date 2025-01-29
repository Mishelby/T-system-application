package org.example.logisticapplication.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CargoGenerateInfo {
    private static final String DEFAULT_NAME = "CARGO-";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Integer DEFAULT_LENGTH = 6;
    private static final Random random = new Random();

    public static String generateCargoNumber() {
        var builder = new StringBuilder(DEFAULT_NAME);

        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            builder.append(CHARACTERS.charAt(random.nextInt(1, 10)));
        }

        return builder.toString();
    }

    public static String generateCargoName() {
        var builder = new StringBuilder(DEFAULT_NAME);

        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            builder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            if (i == DEFAULT_LENGTH / 2) {
                builder.append("-");
            }
        }

        return builder.toString();

    }

    private CargoGenerateInfo() {
    }
}
