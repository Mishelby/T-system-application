package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverWithoutTruckDto(
        Long driverId,
        String name,
        String secondName,
        String currentCityName
) {
}
