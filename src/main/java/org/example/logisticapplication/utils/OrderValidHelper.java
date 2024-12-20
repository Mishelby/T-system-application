package org.example.logisticapplication.utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.RoutePoint.OperationType;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.CargoRepository;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderValidHelper {
    private final OrderRepository orderRepository;
    private final RoutePointMapper routePointMapper;
    private final CityRepository cityRepository;
    private final CargoRepository cargoRepository;

    @Transactional(readOnly = true)
    public void isOrderHasBeenCreated(
            CreateOrderRequest orderRequest
    ) {
        if (orderRepository.existsOrderEntityByUniqueNumber(orderRequest.uniqueNumber())) {
            throw new IllegalArgumentException(
                    "Order with number=%s already exists"
                            .formatted(orderRequest.uniqueNumber())
            );
        }
    }

    @Transactional(readOnly = true)
    public void validateOrderAndFetch(
            Long orderId
    ) {
        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException(
                    "Order with id=%s not Found"
                            .formatted(orderId)
            );
        }
    }

    @Transactional(readOnly = true)
    public CityEntity getCityEntity(
            RoutePoint routePoint
    ) {
        return cityRepository.findById(routePoint.cityId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "City Map with id=%s Not Found"
                                .formatted(routePoint.cityId())
                )
        );
    }

    @Transactional(readOnly = true)
    public List<RoutePointEntity> getRoutePointEntities(CreateOrderRequest orderRequest) {
        return orderRequest.routePoints()
                .stream()
                .map(routePoint -> {
                    var cityEntity = getCityEntity(routePoint);

                    var cargoEntity = cargoRepository.findAllById(routePoint.cargoId());

                    if (cargoEntity.size() != routePoint.cargoId().size()) {
                        throw new EntityNotFoundException(
                                "Cargo with id=%s Not Found"
                                        .formatted(routePoint.cargoId())
                        );
                    }

                    return routePointMapper.toEntity(routePoint, cityEntity, cargoEntity);
                })
                .toList();
    }


    public static Set<Long> getIdForUnloadedCargos(List<RoutePointEntity> routePointEntities) {
        return routePointEntities.stream()
                .filter(entity -> entity.getOperationType().equals(OperationType.UNLOADING.toString()))
                .flatMap(entity -> entity.getCargo().stream())
                .map(CargoEntity::getId)
                .collect(Collectors.toSet());
    }

    public static Set<Long> getIdForLoadedCargos(List<RoutePointEntity> routePointEntities) {
        return routePointEntities.stream()
                .filter(entity -> entity.getOperationType().equals(OperationType.LOADING.toString()))
                .flatMap(entity -> entity.getCargo().stream())
                .map(CargoEntity::getId)
                .collect(Collectors.toSet());
    }

}
