package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Driver.DriverStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class BusinessLogicHelper {
    /**
     * Validates that the given status is valid and throws
     * an IllegalArgumentException if it is not.
     *
     * @param status the status to be validated
     */
    public static void isValidStatus(String status) {
        boolean isValid = Arrays.stream(DriverStatus.values())
                .anyMatch(value -> value.name().equalsIgnoreCase(status));

        // If the status is not valid, throw an exception
        if (!isValid) {
            throw new IllegalArgumentException(
                    "Invalid status value =%s"
                            .formatted(status)
            );
        }
    }
}
