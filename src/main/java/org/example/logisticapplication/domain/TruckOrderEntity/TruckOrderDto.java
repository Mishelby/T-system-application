package org.example.logisticapplication.domain.TruckOrderEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruckOrderDto(
        @Null
        Long id,
        Long orderId,
        Long truckId
) {
}
