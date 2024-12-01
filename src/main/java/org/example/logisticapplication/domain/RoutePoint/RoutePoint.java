package org.example.logisticapplication.domain.RoutePoint;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoutePoint(
        Long id,
        String city,
        Long cargoId,
        String operationType
) {
}
