package org.example.logisticapplication.domain.Truck;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateTruckDto(
        @NotNull
        Integer countOfSeats,
        @NotNull
        String currentCityName,
        @NotNull
        Double capacity,
        @NotNull
        String status
) {
}
