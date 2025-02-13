package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.RoutePoint.BaseRoutePoints;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SendOrderForSubmittingDto(
        Long userId,
        List<BaseRoutePoints> routePoints,
        LocalDate departureDate
) {
}
