package org.example.logisticapplication.domain.Truck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.DriverEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Truck(
        Long id,

        String registrationNumber,

        Integer driversShift,

        String status,

        @NotNull(message = "Capacity cannot be null")
        @Positive(message = "Capacity must be positive")
        @Min(value = 100)
        Double capacity,

        @JsonProperty("cityId")
        Long currentCityId
) {
}
