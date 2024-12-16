package org.example.logisticapplication.domain.Order;

import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrder;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrder;

import java.util.List;

public record OrderInfo(
        Long id,
        String uniqueNumber,
        String orderStatus,
        String countyMapName,
        List<RoutePointInfoDto> routePoints,
        List<TruckOrder> truckOrder,
        List<DriverOrder> driverOrder
) {
    public OrderInfo(
            Long id,
            String uniqueNumber,
            String orderStatus,
            String countyMapName,
            List<RoutePointInfoDto> routePoints,
            List<TruckOrder> truckOrder,
            List<DriverOrder> driverOrder
    ) {
        this.id = id;
        this.uniqueNumber = uniqueNumber;
        this.orderStatus = orderStatus;
        this.countyMapName = countyMapName;
        this.routePoints = routePoints;
        this.truckOrder = truckOrder;
        this.driverOrder = driverOrder;
    }
}
