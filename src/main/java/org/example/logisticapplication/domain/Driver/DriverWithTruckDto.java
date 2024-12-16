package org.example.logisticapplication.domain.Driver;

import org.example.logisticapplication.domain.Truck.TruckEntity;

public record DriverWithTruckDto(
        DriverEntity driver,
        TruckEntity truck
) {
    public DriverWithTruckDto(
            DriverEntity driver,
            TruckEntity truck
    ) {
        this.driver = driver;
        this.truck = truck;
    }
}
