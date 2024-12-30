package org.example.logisticapplication.domain.DriverOrderEntity;

import org.example.logisticapplication.domain.Driver.DriverAllInfoDto;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;

import java.util.List;

public record DriversAndTrucksForOrderDto(
        List<DriverAllInfoDto> drivers,
        List<TruckInfoDto> trucks
) {
}
