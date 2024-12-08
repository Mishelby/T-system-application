package org.example.logisticapplication.utils;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Truck.TruckEntity;
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
