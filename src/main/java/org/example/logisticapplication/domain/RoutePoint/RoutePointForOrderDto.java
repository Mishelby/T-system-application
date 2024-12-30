package org.example.logisticapplication.domain.RoutePoint;

import org.example.logisticapplication.domain.Cargo.CargoForOrderDto;

import java.util.List;

public record RoutePointForOrderDto(
        String cityName,
        List<CargoForOrderDto> cargos,
        String operationType,
        Long distance
) {
}
