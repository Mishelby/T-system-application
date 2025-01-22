package org.example.logisticapplication.domain.RoutePoint;

import org.example.logisticapplication.domain.Cargo.CargoInfoDto;

import java.util.Set;

public record RoutePointInfoDto(
        String cityName,
        Set<CargoInfoDto> cargoInfo,
        String operationType,
        Integer distance
) {
}
