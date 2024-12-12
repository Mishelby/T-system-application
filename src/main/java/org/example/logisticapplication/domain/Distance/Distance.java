package org.example.logisticapplication.domain.Distance;

public record Distance(
        Long id,

        Long fromCityId,

        Long toCityId,

        Integer distance
) {
}
