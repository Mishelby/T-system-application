package org.example.logisticapplication.domain.Truck;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MainTruckInfoDto(
        String registrationNumber,
        Map<String, Long> driversInfo
) {
}
