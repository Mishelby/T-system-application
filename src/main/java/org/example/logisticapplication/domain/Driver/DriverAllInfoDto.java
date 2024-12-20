package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.logisticapplication.domain.City.CityInfoDto;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverAllInfoDto(
        Long id,

        String name,

        String secondName,

        Long personNumber,

        @JsonProperty("numOfHoursWorked")
        Integer numberOfHoursWorked,

        String status,

        CityInfoDto currentCityInfo,

        TruckInfoDto currentTruckInfo
) {
}
