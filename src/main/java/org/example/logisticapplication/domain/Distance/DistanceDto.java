package org.example.logisticapplication.domain.Distance;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DistanceDto(
        @Null
        Long id,

        Long fromCityId,

        Long toCityId,

        Integer distance,

        Long countryMapId
) {
}
