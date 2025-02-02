package org.example.logisticapplication.domain.Truck;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruckInfoDto(
        Long id,
        String registrationNumber,
        Double driversShift,
        String status,
        Double capacity,
        String currentCityName
) {
}
