package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.RoutePoint.BaseRoutePoints;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SendOrderForSubmittingDto(
        Long userId,
        BaseRoutePoints routePoint,
        LocalDate departureDate
) {
}
