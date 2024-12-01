package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Order(
        Long id,
        String uniqueNumber,
        String orderStatus,
        List<RoutePoint> routePoints,
        Long truckId,
        List<Long> driversId
) {
}
