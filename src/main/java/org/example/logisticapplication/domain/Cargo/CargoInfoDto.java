package org.example.logisticapplication.domain.Cargo;


public record CargoInfoDto(
        String number,
        String name,
        Double weightKg,
        String status
) {
}
