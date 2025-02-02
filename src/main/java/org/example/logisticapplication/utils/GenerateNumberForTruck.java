package org.example.logisticapplication.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GenerateNumberForTruck {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final Integer DEFAULT_LENGTH = 6;
    private static final Random random = new Random();

    public static String generateNumberForTruck() {
        int step = Math.divideExact(DEFAULT_LENGTH, 2);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            result.append(CHARACTERS.charAt(random.nextInt(0, CHARACTERS.length())))
                    .append(DIGITS.charAt(random.nextInt(0, DIGITS.length())));

            if (i == step) {
                result.append("-");
            }
        }

        return result.toString();
    }

    private GenerateNumberForTruck() {
    }
}
