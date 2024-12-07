package org.example.logisticapplication.domain.RoutePoint;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoutePoint(
        Long id,
        Long cityId,
        Long cargoId,
        String operationType
) {
}
