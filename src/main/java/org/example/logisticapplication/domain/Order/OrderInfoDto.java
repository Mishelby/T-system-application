package org.example.logisticapplication.domain.Order;

import jakarta.validation.constraints.Null;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrder;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrder;

import java.util.List;

public record OrderInfoDto(
        @Null
        Long id,
        String uniqueNumber,
        String orderStatus,
        String countyMapName,
        List<RoutePointInfoDto> routePoints,
        List<TruckOrder> truckOrder,
        List<DriverOrder> driverOrder
) {
}
