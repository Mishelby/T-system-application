package org.example.logisticapplication.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Order.OrderDto;
import org.example.logisticapplication.service.OrderService;
import org.example.logisticapplication.utils.OrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/create-order")
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody Order order
    ) {
        log.info("Get request for creating order: {}", order);
        var newOrder = orderService.createOrder(order);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        orderMapper.toDto(newOrder)
                );
    }
}
