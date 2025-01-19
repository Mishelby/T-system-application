package org.example.logisticapplication.domain.Order;


import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;

import java.util.List;

public record CreateBaseOrder(
        List<RoutePointInfoDto> routePointInfoDto
) {
}
