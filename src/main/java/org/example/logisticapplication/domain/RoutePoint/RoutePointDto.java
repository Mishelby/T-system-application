package org.example.logisticapplication.domain.RoutePoint;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoutePointDto(
        @Null
        Long id,
        Long cityId,
        List<Long> cargoId,
        String operationType
) {
}
