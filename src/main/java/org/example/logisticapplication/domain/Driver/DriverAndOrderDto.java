package org.example.logisticapplication.domain.Driver;

import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;

public record DriverAndOrderDto(
        DriverEntity driver,
        OrderEntity order,
        CargoEntity cargo
) {
}
