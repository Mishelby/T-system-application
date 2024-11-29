package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;
import org.example.logisticapplication.domain.RoutePoint.RoutePointDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderDto(
        @Null
        Long id,
        String uniqueNumber,
        RoutePointDto routePoint,
        Integer truckId,
        List<Integer> driversId
) {
}
