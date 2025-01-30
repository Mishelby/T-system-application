package org.example.logisticapplication.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CargoGenerateInfo {
    private static final String DEFAULT_NAME = "CARGO-";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final Integer DEFAULT_LENGTH = 6;
    private static final Random random = new Random();

    public static String generateCargoNumber() {
        return generateCargoName();
    }

    public static String generateCargoName() {
        var builder = new StringBuilder(DEFAULT_NAME);

        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            builder.append(CHARACTERS.charAt(random.nextInt(0,CHARACTERS.length())))
                    .append(DIGITS.charAt(random.nextInt(0, DIGITS.length())));
            if (i == ((DEFAULT_LENGTH / 2) - 1)) {
                builder.append("-");
            }
        }

        return builder.toString();

    }

    private CargoGenerateInfo() {
    }
}
