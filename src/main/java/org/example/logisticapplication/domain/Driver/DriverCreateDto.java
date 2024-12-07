package org.example.logisticapplication.domain.Driver;

import jakarta.validation.constraints.Null;

public record DriverCreateDto(
        @Null
        Long id,

        String name,

        String secondName,

        Long personNumber
) {
}
