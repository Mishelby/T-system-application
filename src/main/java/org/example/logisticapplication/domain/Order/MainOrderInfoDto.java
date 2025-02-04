package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Cargo.MainCargoInfoDto;
import org.example.logisticapplication.domain.Driver.MainDriverInfoDto;
import org.example.logisticapplication.domain.RoutePoint.MainRoutePointInfoDto;
import org.example.logisticapplication.domain.Truck.MainTruckInfoDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MainOrderInfoDto(
        String uniqueNumber,
        String orderStatus,
        String countyMapName,
        List<MainRoutePointInfoDto> mainRoutePointInfo,
        List<MainDriverInfoDto> mainDriverInfo,
        List<MainTruckInfoDto> mainTruckInfo
) {
}
