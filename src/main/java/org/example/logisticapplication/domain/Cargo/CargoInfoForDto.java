package org.example.logisticapplication.domain.Cargo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CargoInfoForDto(
        BigDecimal weight,
        String cargoName,
        String cargoNumber,
        String cargoStatus
) {
}
