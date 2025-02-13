package org.example.logisticapplication.domain.CityStationEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateStationDto(
        String cityName,
        String stationName
) {
}
