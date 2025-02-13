package org.example.logisticapplication.domain.City;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CityWithStationsDto(
        String cityName,
        List<String> stationsNames
) {
}
