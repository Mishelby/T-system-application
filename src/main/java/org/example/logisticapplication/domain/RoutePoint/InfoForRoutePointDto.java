package org.example.logisticapplication.domain.RoutePoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Cargo.BaseCargoDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InfoForRoutePointDto(
        String cityFrom,
        String cityTo,
        BaseCargoDto cargo
) {
}
