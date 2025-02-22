package org.example.logisticapplication.domain.CityStationDistance;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CityStationDistanceDto(
        String countryMapName,
        String stationFrom,
        String stationTo,
        Double distance
) {
}
