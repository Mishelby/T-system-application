package org.example.logisticapplication.utils;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class CargoGenerateInfo {
    private static final String DEFAULT_NAME = "Cargo";
    private static final String DEFAULT_SYMBOLS = "*&%$#@";
    private static final Integer DEFAULT_LENGTH = 6;
    private static final AtomicLong CARGO_ID_GENERATOR = new AtomicLong();
    private static final Random random = new Random();

    public static String generateCargoNumber() {
        return DEFAULT_NAME.concat("-" + CARGO_ID_GENERATOR.incrementAndGet());
    }

    public static String generateCargoName() {
        var builder = new StringBuilder();

        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            builder.append(random.nextInt(1, 10))
                    .append(DEFAULT_NAME.charAt(random.nextInt(0, DEFAULT_NAME.length())))
                    .append(DEFAULT_SYMBOLS.charAt(random.nextInt(0, DEFAULT_NAME.length())));
            if(i % 3 == 0){
                builder.append("-");
            }
        }

        return builder.toString();

    }

    private CargoGenerateInfo() {
    }
}
