package org.example.logisticapplication.domain.Distance;

import jakarta.validation.constraints.Null;

public record Distance(
        Long id,

        Long fromCityId,

        Long toCityId,

        Integer distance,

        Long countryMapId
) {
}
