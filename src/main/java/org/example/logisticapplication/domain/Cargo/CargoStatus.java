package org.example.logisticapplication.domain.Cargo;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CargoStatus {
    PREPARED("PREPARED"),
    SHIPPED("SHIPPED"),
    DELIVERED("DELIVERED"),
    NOT_SHIPPED("NOT_SHIPPED"),;

    final String name;

    CargoStatus(String name) {
        this.name = name;
    }

    public static boolean isValid(
            String status
    ) {
        return Arrays.stream(CargoStatus.values())
                .anyMatch(cargoStatus -> cargoStatus.name().equals(status));
    }
}
