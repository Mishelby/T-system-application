package org.example.logisticapplication.domain.Order;

import org.example.logisticapplication.domain.RoutePoint.RoutePoint;

import java.util.List;

public record CreateOrderRequest(
        Long id,
        String uniqueNumber,
        String orderStatus,
        Long countyMapId,
        List<RoutePoint> routePoints
) {
}
