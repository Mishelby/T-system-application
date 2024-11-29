package org.example.logisticapplication.domain.Distance;

import org.springframework.stereotype.Component;

@Component
public class DistanceDtoConverter {

    public DistanceDto toDto(
            Distance distance
    ) {
        return new DistanceDto(
                distance.id(),
                distance.fromCityId(),
                distance.toCityId(),
                distance.distance()
        );
    }

    public Distance toDomain(
            DistanceDto distance
    ) {
        return new Distance(
                distance.id(),
                distance.fromCityId(),
                distance.toCityId(),
                distance.distance()
        );
    }
}
