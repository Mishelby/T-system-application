package org.example.logisticapplication.domain.Truck;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruckInfoForOrderDto(
        Long id,
        String truckNumber,
        String currentCityName,
        Long countOfSeats,
        Long averageSpeed
) {
}
