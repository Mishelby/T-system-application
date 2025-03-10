package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.OrderTimeEntity.OrderTimeEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.repository.OrderDistanceRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.repository.OrderTimeRepository;
import org.example.logisticapplication.utils.DriverInfoForUserEvent;
import org.example.logisticapplication.utils.TrucksSelectedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderTimeCalculationService {
    private final OrderRepository orderRepository;
    private final OrderDistanceRepository orderDistanceRepository;
    private final OrderTimeRepository orderTimeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @Transactional
    @Async
    public void onTrucksSelected(TrucksSelectedEvent event) {
        var orderNumber = event.getOrderNumber();
        calculateDeliveryTime(orderNumber);
    }

    protected void calculateDeliveryTime(
            String orderNumber
    ) {
        var orderEntity = orderRepository.findOrderEntityByNumber(orderNumber).orElseThrow();
        var orderDistanceEntity = orderDistanceRepository.findOrderDistanceEntity(orderEntity.getId()).orElseThrow(
                () -> new EntityNotFoundException("Order distance not found for order number = %s "
                        .formatted(orderNumber))
        );

        var truckOrders = orderEntity.getTruckOrders().stream().toList();
        double time = 0;

        switch (truckOrders.size()) {
            case 1 -> time = orderDistanceEntity.getDistance().getDistance() / truckOrders.getFirst().getTruck().getAverageSpeed();

            case 2 -> time = orderDistanceEntity.getDistance().getDistance() / (
                    truckOrders.stream()
                            .map(TruckOrderEntity::getTruck)
                            .map(TruckEntity::getAverageSpeed)
                            .mapToDouble(Double::doubleValue)
                            .average())
                    .orElseThrow();

            default -> time = 10.0;

        }

        double finalTime = time;
        truckOrders.forEach(truckOrder -> orderTimeRepository.save(
                new OrderTimeEntity(orderNumber, finalTime, truckOrder.getTruck().getId())
        ));

        eventPublisher.publishEvent(new DriverInfoForUserEvent(this, orderNumber, time));
    }
}
