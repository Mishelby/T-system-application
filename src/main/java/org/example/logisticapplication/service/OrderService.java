package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.repository.RoutePointRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.example.logisticapplication.utils.OrderMapper;
import org.example.logisticapplication.utils.RoutePointMapper;
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
    private final TruckRepository truckRepository;
    private final RoutePointRepository routePointRepository;
    private final DriverRepository driverRepository;
    private final RoutePointMapper routePointMapper;

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

        if (order.routePoints().isEmpty()) {
            throw new IllegalArgumentException(
                    "No route points found for order=%s"
                            .formatted(order)
            );
        }

        var truckEntity = truckRepository.findById(order.truckId()).orElseThrow(
                () -> new IllegalArgumentException(
                        "No truck found for order=%s"
                                .formatted(order)
                )
        );

        var allDriversById = driverRepository.findAllById(order.driversId());
        var orderEntity = orderMapper.toEntity(order, allDriversById, truckEntity);

        if (!allDriversById.isEmpty()) {
            orderEntity.setDrivers(allDriversById);
        }

        var savedOrder = orderRepository.save(orderEntity);

        return orderMapper.toDomain(savedOrder);
    }
}