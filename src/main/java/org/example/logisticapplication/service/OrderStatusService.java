package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Order.OrderStatus;
import org.example.logisticapplication.domain.OrderStatusEntity.OrderStatusEntity;
import org.example.logisticapplication.repository.OrderStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;

    @Transactional
    public synchronized ConcurrentMap<OrderStatus, OrderStatusEntity> getOrderStatus() {
        var allOrderStatus = orderStatusRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        OrderStatusEntity::getStatus,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        ConcurrentHashMap::new
                ));

        var statusValues = OrderStatus.values();

        if(allOrderStatus.size() < statusValues.length) {
            Arrays.stream(statusValues)
                    .filter(orderStatus -> !allOrderStatus.containsKey(orderStatus))
                    .forEach(orderStatus -> {
                        var savedStatus = orderStatusRepository.save(new OrderStatusEntity(orderStatus));
                        allOrderStatus.put(orderStatus, savedStatus);
                    });
        }

        return allOrderStatus;
    }
}
