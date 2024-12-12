package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Driver.DriverDto;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.OrderDto;
import org.example.logisticapplication.domain.Order.OrderStatusDto;
import org.example.logisticapplication.domain.Truck.TruckDto;
import org.example.logisticapplication.service.OrderService;
import org.example.logisticapplication.utils.DriverMapper;
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
    private final DriverMapper driverMapper;

    @PostMapping("/create-order")
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody CreateOrderRequest order
    ) {
        log.info("Get request for creating order: {}", order);
        var newOrder = orderService.createBaseOrder(order);

        return ResponseEntity.ok(orderMapper.toDto(newOrder));
    }

    @GetMapping("/{id}/trucks")
    public ResponseEntity<List<TruckDto>> getTruckForOrder(
            @PathVariable("id") Long orderId
    ) {
        log.info("Get request for getting trucks for order");
        var truckForOrder = orderService.findTruckForOrder(orderId);

        return ResponseEntity.ok(
                truckForOrder.stream()
                        .map(truckMapper::toDto)
                        .toList());
    }

    @GetMapping("/{id}/trucks/drivers")
    public ResponseEntity<List<DriverDto>> getDriversForOrder(
            @PathVariable("id") Long orderId
    ){
        log.info("Get request for getting drivers for order");
        var driversForOrder = orderService.showDriversForOrder(orderId);

        return ResponseEntity.ok(
                driversForOrder.stream()
                .map(driverMapper::toDto)
                .toList()
        );
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<OrderStatusDto> getOrderStatusById(
            @PathVariable("id") Long orderId
    ){
        log.info("Get request for getting status for order");
        var orderStatusById = orderService.getOrderStatusById(orderId);

        return ResponseEntity.ok(orderStatusById);
    }
}
