package org.example.logisticapplication.domain.RoutePoint;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoutePointInfoForUserDto(
        String cityFrom,
        String stationFrom,
        String cityTo,
        String stationTo,
        Double weight,
        Double distance
) {
}
