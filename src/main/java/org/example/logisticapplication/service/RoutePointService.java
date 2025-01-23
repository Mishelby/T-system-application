package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.repository.CargoRepository;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.RoutePointRepository;
import org.example.logisticapplication.mapper.RoutePointMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoutePointService {
    private final RoutePointRepository routePointRepository;
    private final RoutePointMapper routePointMapper;
    private final CityRepository cityRepository;
    private final CargoRepository cargoRepository;


    @Transactional
    public RoutePoint addNewRoutePoint(
            RoutePoint routePoint
    ) {

        var cityEntity = cityRepository.findById(routePoint.cityId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "City with id=%s not found"
                                .formatted(routePoint.cityId())
                )
        );

        var cargoEntity = cargoRepository.findAllById(routePoint.cargoId());
        var routePointentity = routePointMapper.toEntity(routePoint, cityEntity, cargoEntity);
        var savedRoutePoint = routePointRepository.save(routePointentity);

        return routePointMapper.toDomain(savedRoutePoint);
    }
}
