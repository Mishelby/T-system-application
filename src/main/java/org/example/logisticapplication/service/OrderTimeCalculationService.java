package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.OrderDistanceEntity.OrderDistanceEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.repository.CityStationDistanceRepository;
import org.example.logisticapplication.repository.OrderDistanceRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.utils.TrucksSelectedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderTimeCalculationService {
    private final OrderRepository orderRepository;
    private final OrderDistanceRepository orderDistanceRepository;

    @EventListener
    public void onTrucksSelected(TrucksSelectedEvent event) {
        var orderNumber = event.getOrderNumber();
        var orderEntity = orderRepository.findOrderEntityByNumber(orderNumber).orElseThrow();
        var orderDistanceEntity = orderDistanceRepository.findOrderDistanceEntity(orderEntity.getId()).orElseThrow();

        var truckOrders = orderEntity.getTruckOrders().stream().toList();
        double time = 0;

        switch (truckOrders.size()) {
            case 1 -> {
                time = orderDistanceEntity.getDistance().getDistance() / truckOrders.getFirst().getTruck().getAverageSpeed();
            }
            case 2 -> {
                time = orderDistanceEntity.getDistance().getDistance() / (
                        truckOrders.stream()
                                .map(TruckOrderEntity::getTruck)
                                .map(TruckEntity::getAverageSpeed)
                                .mapToDouble(Double::doubleValue).sum() / truckOrders.size()
                );
            }
            default -> {
                time = 10.0;
            }
        }
    }
}
