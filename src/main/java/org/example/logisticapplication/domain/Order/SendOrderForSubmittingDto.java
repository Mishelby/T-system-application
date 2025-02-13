package org.example.logisticapplication.domain.Order;

import org.example.logisticapplication.domain.RoutePoint.BaseRoutePoints;

import java.util.List;

public record SendOrderForSubmittingDto(
        Long userId,
        List<BaseRoutePoints> routePoints
) {
}
