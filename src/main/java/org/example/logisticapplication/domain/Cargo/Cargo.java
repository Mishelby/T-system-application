package org.example.logisticapplication.domain.Cargo;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Cargo(
        Long id,

        Integer cargoNumber,

        String cargoName,

        Integer cargoMass,

        String cargoStatus
) {
}
