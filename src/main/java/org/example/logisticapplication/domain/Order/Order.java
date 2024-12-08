package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrder;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Order(
        Long id,
        String uniqueNumber,
        String orderStatus,
        Long countyMapId,
        List<RoutePoint> routePoints,
        TruckOrder truckOrder,
        DriverOrder driverOrder
) {
}
