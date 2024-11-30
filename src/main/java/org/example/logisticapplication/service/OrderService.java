package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.repository.RoutePointRepository;
import org.example.logisticapplication.utils.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RoutePointRepository routePointRepository;
    private final DriverRepository driverRepository;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Order createOrder(
            Order order
    ) {
        if (orderRepository.existsOrderEntityByUniqueNumber(order.uniqueNumber())) {
            throw new IllegalArgumentException(
                    "Order with number=%s already exists"
                            .formatted(order.uniqueNumber())
            );
        }

        var entity = orderMapper.toEntity(order);

        if (entity.getRoutePoints() != null && !entity.getRoutePoints().isEmpty()) {
            List<RoutePointEntity> allById = routePointRepository.findAllById(order.routePoints());
            entity.setRoutePoints(allById);
        }


        if (order.driversId() != null && !order.driversId().isEmpty()) {
            List<DriverEntity> newDrivers = driverRepository.findAllById(order.driversId());

            List<DriverEntity> existingDrivers = entity.getDrivers()
                    .stream()
                    .filter(existingDriver -> !newDrivers.contains(existingDriver))
                    .toList();

            if (newDrivers.size() != existingDrivers.size()) {
                entity.setDrivers(newDrivers);
            } else {
                throw new IllegalArgumentException(
                        "There are duplicate drivers in this order %s"
                                .formatted(order.uniqueNumber())
                );
            }
        }

        var savedOrder = orderRepository.save(entity);

        return orderMapper.toDomain(savedOrder);
    }
}
