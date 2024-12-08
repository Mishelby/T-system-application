package org.example.logisticapplication.domain.DriverOrderEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverOrderDto(
        @Null
        Long id,
        Long orderId,
        List<Long> driverId
) {
}
