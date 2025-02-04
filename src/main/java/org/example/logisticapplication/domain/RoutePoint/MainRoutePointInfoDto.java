package org.example.logisticapplication.domain.RoutePoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Cargo.MainCargoInfoDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MainRoutePointInfoDto(
        String cityFrom,
        String cityTo,
        Long distance,
        MainCargoInfoDto cargoInfoDto
) {
}
