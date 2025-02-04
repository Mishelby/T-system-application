package org.example.logisticapplication.domain.Truck;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MainTruckInfoDto(
        String registrationNumber,
        String status,
        Map<String, Long> driversInfo
) {
}
