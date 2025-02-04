package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MainDriverInfoDto(
        String name,
        Integer number,
        String status,
        String truckNumber
) {
}
