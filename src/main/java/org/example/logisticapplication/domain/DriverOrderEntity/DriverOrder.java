package org.example.logisticapplication.domain.DriverOrderEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverOrder(
        Long id,
        Long orderId,
        List<Long> driverId
) {
}
