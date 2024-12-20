package org.example.logisticapplication.domain.Truck;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.DriverEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruckDto(
        @Null
        Long id,

        String registrationNumber,

        Integer driversShift,

        String status,

        @NotEmpty
        Double capacity,

        Long currentCityId
) {
}
