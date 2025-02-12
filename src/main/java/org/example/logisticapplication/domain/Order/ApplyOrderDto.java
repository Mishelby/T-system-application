package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApplyOrderDto(
        Long userId,
        AssignDriversAndTrucksRequest driversAndTrucks
) {
}
