package org.example.logisticapplication.domain.Cargo;

import java.math.BigDecimal;

public record CargoForOrderDto(
        String name,
        Long weight
) {
}
