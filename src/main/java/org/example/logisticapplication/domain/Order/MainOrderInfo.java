package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MainOrderInfo(
        String uniqueNumber,
        String orderStatus,
        String countyMapName,
        List<String> driversNumbers,
        List<TruckInfoDto> truckOrder
) {
}
