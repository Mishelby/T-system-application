package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
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


    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public RoutePoint addNewRoutePoint(
            RoutePoint routePoint
    ) {
        if (routePointRepository.existsRoutePointEntitiesById(routePoint.id())) {
            throw new IllegalArgumentException(
                    "Route point with id=%s already exists"
                            .formatted(routePoint.id()));
        }

        var routePointentity = routePointMapper.toEntity(routePoint);

//        routePointentity.setCargo();

        var savedRoutePoint = routePointRepository.save(
                routePointMapper.toEntity(routePoint)
        );


        return routePointMapper.toDomain(savedRoutePoint);
    }
}
