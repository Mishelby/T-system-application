package org.example.logisticapplication.domain.Driver;

import org.example.logisticapplication.domain.Truck.TruckEntity;

public record DriverWithTruckDto(
        DriverEntity driver,
        TruckEntity truck
) {
}
