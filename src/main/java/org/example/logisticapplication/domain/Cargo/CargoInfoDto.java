package org.example.logisticapplication.domain.Cargo;

import lombok.Getter;

import java.math.BigDecimal;


public record CargoInfoDto(
        String number,
        String name,
        BigDecimal weightKg,
        String status
) {
}
