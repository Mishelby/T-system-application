package org.example.logisticapplication.domain.Distance;

import org.example.logisticapplication.domain.City.CityEntity;
import org.springframework.stereotype.Component;

@Component
public class DistanceEntityConverter {

    public Distance toDomain(
            DistanceEntity entity
    ) {
        return new Distance(
                entity.getId(),
                entity.getFromCity().getId(),
                entity.getToCity().getId(),
                entity.getDistance()
        );
    }

    public DistanceEntity toEntity(
            Distance entity,
            CityEntity fromCity,
            CityEntity toCity
    ) {
        return new DistanceEntity(
                entity.id(),
                fromCity,
                toCity,
                entity.distance()
        );
    }
}
