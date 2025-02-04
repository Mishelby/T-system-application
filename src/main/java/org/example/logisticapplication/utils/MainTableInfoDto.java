package org.example.logisticapplication.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Driver.MainDriverInfoDto;
import org.example.logisticapplication.domain.Order.MainOrderInfoDto;
import org.example.logisticapplication.domain.Truck.MainTruckInfoDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MainTableInfoDto(
        List<MainOrderInfoDto> orderTableInfo,
        List<MainDriverInfoDto> driverInfo,
        List<MainTruckInfoDto> truckInfo,
        Long countOfDrivers,
        Long countOfTrucks
) {
}
