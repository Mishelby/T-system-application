package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverBaseInfoDto(
        String name,
        String secondName,
        Long  personNumber,
        String currentCityName
) {
}
