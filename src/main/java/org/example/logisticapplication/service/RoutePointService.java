package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.repository.CargoRepository;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.RoutePointRepository;
import org.example.logisticapplication.utils.RoutePointMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoutePointService {
    private final RoutePointRepository routePointRepository;
    private final RoutePointMapper routePointMapper;
    private final CityRepository cityRepository;
    private final CargoRepository cargoRepository;


    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public RoutePoint addNewRoutePoint(
            RoutePoint routePoint
    ) {
        if (routePointRepository.existsRoutePointEntitiesById(routePoint.id())) {
            throw new IllegalArgumentException(
                    "Route point with id=%s already exists"
                            .formatted(routePoint.id()));
        }

        var cityEntity = cityRepository.findById(routePoint.cityId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "City with id=%s not found"
                                .formatted(routePoint.cityId())
                )
        );

        var cargoEntity = cargoRepository.findById(routePoint.cargoId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Cargo with id=%s not found"
                                .formatted(routePoint.cargoId())
                )
        );

        var routePointentity = routePointMapper.toEntity(routePoint, cityEntity, cargoEntity);

        var savedRoutePoint = routePointRepository.save(routePointentity);

        return routePointMapper.toDomain(savedRoutePoint);
    }
}
