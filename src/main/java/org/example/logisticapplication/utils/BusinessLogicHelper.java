package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Driver.DriverStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class BusinessLogicHelper {
    public static void isValidStatus(String status) {
        boolean isValid = Arrays.stream(DriverStatus.values())
                .anyMatch(value -> value.name().equalsIgnoreCase(status));

        if (!isValid) {
            throw new IllegalArgumentException("Invalid status value =%s"
                    .formatted(status)
            );
        }
    }
}
