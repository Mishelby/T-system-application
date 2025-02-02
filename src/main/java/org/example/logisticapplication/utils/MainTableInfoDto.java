package org.example.logisticapplication.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Driver.DriverAllInfoDto;
import org.example.logisticapplication.domain.Order.OrderInfo;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MainTableInfoDto(
        List<OrderInfo> orderTableInfo,
        List<DriverAllInfoDto> allDrivers,
        List<TruckInfoDto> allTrucks
) {
}
