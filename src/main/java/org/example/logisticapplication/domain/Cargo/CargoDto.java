package org.example.logisticapplication.domain.Cargo;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CargoDto(
        @Null
        Long id,

        Integer cargoNumber,

        String cargoName,

        Integer cargoMass,

        String cargoStatus
) {
}
