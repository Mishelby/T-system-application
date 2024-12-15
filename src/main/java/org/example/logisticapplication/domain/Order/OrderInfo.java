package org.example.logisticapplication.domain.Order;

import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrder;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrder;

import java.util.List;

public record OrderInfo(
        Long id,
        String uniqueNumber,
        String orderStatus,
        Long countyMapId,
        List<RoutePoint> routePoints,
        List<TruckOrder> truckOrder,
        List<DriverOrder> driverOrder
) {
}
