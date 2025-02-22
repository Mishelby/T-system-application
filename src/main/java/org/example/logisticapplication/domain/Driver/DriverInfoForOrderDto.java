package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverInfoForOrderDto(
        Long id,
        String name,
        String currenCityName,
        Long numOfHoursWorked
) {
}
