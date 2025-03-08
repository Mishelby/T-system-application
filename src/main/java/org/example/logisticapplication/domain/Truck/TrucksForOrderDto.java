package org.example.logisticapplication.domain.Truck;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrucksForOrderDto(
        List<TruckInfoForOrderDto> truckInfo
) {
}
