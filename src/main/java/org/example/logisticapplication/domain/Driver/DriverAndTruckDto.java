package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Truck.TruckDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverAndTruckDto(
        List<DriverAllInfoDto> drivers,
        List<TruckDto> trucks
) {
}
