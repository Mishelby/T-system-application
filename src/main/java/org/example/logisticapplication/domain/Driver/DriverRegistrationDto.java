package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverRegistrationDto(
        @Null
        Long id,

        String name,

        String secondName,

        Long  personNumber,

        @JsonProperty("cityId")
        Long currentCityId,

        @JsonProperty("truckName")
        String currentTruckName
) {
}
