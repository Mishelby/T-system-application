package org.example.logisticapplication.domain.Cargo;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CargoInfoDto(
        String number,
        String name,
        Double weightKg,
        String status
) {
}
