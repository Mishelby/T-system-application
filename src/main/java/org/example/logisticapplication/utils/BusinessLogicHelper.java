package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Driver.DriverStatus;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;
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
    public static void isValidShiftStatus(
            String status
    ) {
        boolean isValid = Arrays.stream(ShiftStatus.values())
                .anyMatch(value -> value.name().equalsIgnoreCase(status));

        if (!isValid) {
            throw new IllegalArgumentException(
                    "Invalid shift status = %s"
                            .formatted(status)
            );
        }
    }

    public static void isValidDriverStatus(
            String status
    ) {
        boolean isValid = Arrays.stream(DriverStatus.values())
                .anyMatch(value -> value.name().equalsIgnoreCase(status));

        if (!isValid) {
            throw new IllegalArgumentException(
                    "Invalid driver status = %s"
                            .formatted(status)
            );
        }
    }
}
