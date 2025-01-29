package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseOrderInfo(
        String uniqueNumber,
        String orderStatus,
        String countyMapName,
        List<RoutePointInfoDto> routePoints
) {

}
