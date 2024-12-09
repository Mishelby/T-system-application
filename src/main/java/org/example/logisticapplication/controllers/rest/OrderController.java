package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.OrderDto;
import org.example.logisticapplication.domain.Truck.TruckDto;
import org.example.logisticapplication.service.OrderService;
import org.example.logisticapplication.utils.OrderMapper;
import org.example.logisticapplication.utils.TruckMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final TruckMapper truckMapper;

    @PostMapping("/create-order")
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody CreateOrderRequest order
    ) {
        log.info("Get request for creating order: {}", order);
        var newOrder = orderService.createBaseOrder(order);

        return ResponseEntity.ok(orderMapper.toDto(newOrder));
    }

    @GetMapping("/{uniqueNumber}/trucks")
    public ResponseEntity<List<TruckDto>> getTruckForOrder(
            @PathVariable("uniqueNumber") String uniqueNumber
    ) {
        log.info("Get request for getting drivers for order");
        var truckForOrder = orderService.findTruckForOrder(uniqueNumber);

        return ResponseEntity.ok(
                truckForOrder.stream()
                        .map(truckMapper::toDto)
                        .toList());
    }
}
