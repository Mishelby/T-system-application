package org.example.logisticapplication.domain.RoutePoint;


import java.util.List;

public record BaseOrder(
        String uniqueNumber,
        String orderStatus,
        List<RoutePointInfoDto> routePoints
) {
}
