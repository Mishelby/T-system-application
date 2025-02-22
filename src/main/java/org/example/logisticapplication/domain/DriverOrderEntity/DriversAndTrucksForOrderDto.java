package org.example.logisticapplication.domain.DriverOrderEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Driver.DriverAllInfoDto;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriversAndTrucksForOrderDto(
        List<DriverAllInfoDto> drivers,
        List<TruckInfoDto> trucks
) {
}
