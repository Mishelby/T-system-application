package org.example.logisticapplication.domain.Truck;

public record TruckForDriverDto(
        Long id,
        String registrationNumber,
        Double capacity
) {
}
