package org.example.logisticapplication.domain.Cargo;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Cargo(
        Long id,

        String number,

        String cargoName,

        BigDecimal weightKg,

        String cargoStatus
) {
}
