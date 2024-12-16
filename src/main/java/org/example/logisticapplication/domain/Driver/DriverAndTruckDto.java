package org.example.logisticapplication.domain.Driver;

import org.example.logisticapplication.domain.Truck.TruckDto;

import java.util.List;


public record DriverAndTruckDto(
        List<DriverDto> drivers,
        List<TruckDto> trucks
) {
    public DriverAndTruckDto(
            List<DriverDto> drivers,
            List<TruckDto> trucks
    ) {
        this.drivers = drivers;
        this.trucks = trucks;
    }
}
