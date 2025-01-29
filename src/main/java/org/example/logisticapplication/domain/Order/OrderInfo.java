package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Driver.DriverOrderInfo;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderInfo(
        String uniqueNumber,
        String orderStatus,
        String countyMapName,
        List<RoutePointInfoDto> routePoints,
        List<TruckInfoDto> truckOrder,
        List<DriverOrderInfo> driverOrder
) {
}
