package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.DriverOrderEntity.DriversAndTrucksForOrderDto;
import org.example.logisticapplication.domain.Order.*;
import org.example.logisticapplication.domain.RoutePoint.BaseRoutePoints;
import org.example.logisticapplication.domain.RoutePoint.RoutePointForOrderDto;
import org.example.logisticapplication.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<OrderInfo> createOrder(
            @RequestBody CreateBaseOrder baseOrder
    ) {
        log.info("Get request for creating order: {}", baseOrder);
        var newOrder = orderService.createBaseOrder(baseOrder);

        return ResponseEntity.ok()
                .body(newOrder);
    }

    @PostMapping("/confirm-order")
    public ResponseEntity<CreateBaseOrder> confirmOrder(
            @RequestBody BaseRoutePoints routePoint
    ){
        log.info("Get request for confirming order: {}", routePoint);
        var createBaseOrder = orderService.sendBaseOrder(routePoint);

        return ResponseEntity.ok()
                .body(createBaseOrder);
    }


    @GetMapping("/trucks-drivers")
    public ResponseEntity<DriversAndTrucksForOrderDto> getTruckAndDriversForOrder(
            @RequestBody List<RoutePointForOrderDto> routePointDto
    ) {
        log.info("Get request for getting trucks for order");
        var truckForOrder = orderService.findTrucksAndDriversForOrder(routePointDto);

        return ResponseEntity.ok()
                .body(truckForOrder);
    }


    @GetMapping("/{id}/status")
    public ResponseEntity<OrderStatusDto> getOrderStatusById(
            @PathVariable("id") Long orderId
    ) {
        log.info("Get request for getting status for order");
        var orderStatusById = orderService.getOrderStatusById(orderId);

        return ResponseEntity.ok()
                .body(orderStatusById);
    }

    @GetMapping("/submitting")
    public ResponseEntity<List<OrderInfo>> getOrdersForSubmit(){
        log.info("Get request for submitting orders");
        var orderInfo = orderService.gerOrdersForSubmit();

        return ResponseEntity.ok()
                .body(orderInfo);
    }
}
