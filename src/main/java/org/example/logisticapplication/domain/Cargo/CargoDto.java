package org.example.logisticapplication.domain.Cargo;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CargoDto(
        @Null
        Long id,

        String number,

        String cargoName,

        @NotNull
        BigDecimal weightKg,

        String cargoStatus
) {
}
