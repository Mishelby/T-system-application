package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Driver(
        Long id,

        String name,

        String secondName,

        Long  personNumber,

        @JsonProperty("numOfHoursWorked")
        Integer numberOfHoursWorked,

        String status,

        @JsonProperty("cityId")
        Long currentCityId,

        @JsonProperty("truckId")
        Long currentTruckId
) {
}
