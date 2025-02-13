package org.example.logisticapplication.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Order.OrderStatus;
import org.example.logisticapplication.domain.OrderStatusEntity.OrderStatusEntity;
import org.example.logisticapplication.repository.OrderStatusRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;

    @PostConstruct
    public void init() {
        var allOrderStatus = orderStatusRepository.findAll();
        var statusValues = OrderStatus.values();

        if(allOrderStatus.size() < statusValues.length) {
            Arrays.stream(statusValues)
                    .map(OrderStatus::getName)
                    .filter(status -> allOrderStatus.stream().noneMatch(orderStatus ->
                            orderStatus.getStatusName().equals(status)))
                    .forEach(status -> orderStatusRepository.save(new OrderStatusEntity(status)));
        }
    }
}
