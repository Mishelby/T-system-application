package org.example.logisticapplication.domain.Cargo;

import java.util.Arrays;

public enum CargoStatus {
    PREPARED,
    SHIPPED,
    DELIVERED,
    NOT_SHIPPED;

    public static boolean isValid(
            String status
    ) {
        return Arrays.stream(CargoStatus.values())
                .anyMatch(cargoStatus -> cargoStatus.name().equals(status));
    }
}
