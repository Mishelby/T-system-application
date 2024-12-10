package org.example.logisticapplication.domain.Cargo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)// to ignore null values
public record CargoStatusDto(
        String number,
        String status
) {
}
