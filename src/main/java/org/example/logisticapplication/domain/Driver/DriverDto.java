package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverDto(
        @Null
        Long id,

        String name,

        String secondName,

        String personNumber,

        @JsonProperty("numOfHoursWorked")
        Integer numberOfHoursWorked,

        String status,

        @JsonProperty("cityId")
        Long currentCityId,

        @JsonProperty("truckId")
        Long currentTruckId
) {
}
