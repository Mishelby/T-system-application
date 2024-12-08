package org.example.logisticapplication.domain.TruckOrderEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruckOrder(
        Long id,
        Long orderId,
        Long truckId
) {
}
