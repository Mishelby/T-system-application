package org.example.logisticapplication.domain.Driver;

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
