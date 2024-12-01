package org.example.logisticapplication.domain.RoutePoint;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoutePointDto(
        @Null
        Long id,
        String city,
        Long cargoId,
        String operationType
) {
}
