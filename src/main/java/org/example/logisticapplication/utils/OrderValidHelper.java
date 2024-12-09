package org.example.logisticapplication.utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.CargoRepository;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderValidHelper {
    private final OrderRepository orderRepository;
    private final TruckRepository truckRepository;
    private final RoutePointMapper routePointMapper;
    private final CityRepository cityRepository;
    private final CargoRepository cargoRepository;

    @Transactional(readOnly = true)
    public TruckEntity findTruckEntityById(
            Order order
    ) {
        return truckRepository.findById(order.truckOrder().truckId()).orElseThrow(
                () -> new IllegalArgumentException(
                        "No truck found for order=%s"
                                .formatted(order)
                )
        );
    }

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
            String uniqueNumber
    ) {
        if (!orderRepository.existsByUniqueNumber(uniqueNumber)) {
            throw new EntityNotFoundException(
                    "Order with unique number=%s not Found"
                            .formatted(uniqueNumber)
            );
        }
    }

    @Transactional(readOnly = true)
    public List<RoutePointEntity> getRoutePointEntities(CreateOrderRequest orderRequest) {
        return orderRequest.routePoints()
                .stream()
                .map(routePoint -> {
                    var cityEntity = cityRepository.findById(routePoint.cityId()).orElseThrow(
                            () -> new EntityNotFoundException(
                                    "City Map with id=%s Not Found"
                                            .formatted(routePoint.cityId())
                            )
                    );

                    var cargoEntity = cargoRepository.findAllById(routePoint.cargoId());

                    //TODO: check if cargo is in correct city
                    if(cargoEntity.size() != routePoint.cargoId().size()) {
                        throw new EntityNotFoundException(
                                "Cargo with id=%s Not Found"
                                        .formatted(routePoint.cargoId())
                        );
                    }

                    return routePointMapper.toEntity(routePoint, cityEntity, cargoEntity);
                })
                .toList();
    }

    public void isRoutePointsListEmpty(
            Order order
    ) {
        if (order.routePoints().isEmpty()) {
            throw new IllegalArgumentException(
                    "No route points found for order=%s"
                            .formatted(order)
            );
        }
    }

    public void checkDriversById(
            List<DriverEntity> drivers,
            Order order
    ) {
        if(drivers.isEmpty()){
            throw new IllegalArgumentException(
                    "No drivers found for order id=%s"
                            .formatted(order.id())
            );
        }
    }


}
