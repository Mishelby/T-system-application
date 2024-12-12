package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;

import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverInfoDto(
        @JsonProperty("number")
        String driverNumber,

        Set<String> coDriverNumber,

        @JsonProperty("regTruckNumber")
        String registrationTruckNumber,

        String orderId,

        List<Long> routePointsId
) {

}
