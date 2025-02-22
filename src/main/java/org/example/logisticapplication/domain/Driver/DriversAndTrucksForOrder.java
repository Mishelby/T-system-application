package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Truck.TruckInfoForOrderDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriversAndTrucksForOrder(
        List<DriverInfoForOrderDto> driverInfo,
        List<TruckInfoForOrderDto> truckInfo
) {
}
