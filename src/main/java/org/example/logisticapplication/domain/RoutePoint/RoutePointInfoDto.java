package org.example.logisticapplication.domain.RoutePoint;

import org.example.logisticapplication.domain.Cargo.CargoInfoDto;

import java.util.List;

public record RoutePointInfoDto(
        String cityName,
        List<CargoInfoDto> cargoInfo,
        String operationType

) {
}
