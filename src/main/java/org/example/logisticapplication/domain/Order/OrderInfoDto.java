package org.example.logisticapplication.domain.Order;

import jakarta.validation.constraints.Null;
import org.example.logisticapplication.domain.Driver.DriverOrderInfo;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;

import java.util.List;

public record OrderInfoDto(
        @Null
        Long id,
        String uniqueNumber,
        String orderStatus,
        String countyMapName,
        List<RoutePointInfoDto> routePoints,
        List<TruckInfoDto> truckOrder,
        List<DriverOrderInfo> driverOrder
) {
}
