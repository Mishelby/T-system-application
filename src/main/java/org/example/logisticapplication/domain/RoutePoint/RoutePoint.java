package org.example.logisticapplication.domain.RoutePoint;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoutePoint(
        Long id,
        Long cityId,
        List<Long> cargoId,
        String operationType
) {
}
