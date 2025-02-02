package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverOrderInfo(
        Long id,
        String name,
        String secondName,
        String personNumber,
        String numberOfHoursWorked,
        String status,
        String currentLocation,
        String currentTruckNumber
) {
}
