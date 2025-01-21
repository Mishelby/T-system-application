package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.DriverOrderEntity.DriversAndTrucksForOrderDto;
import org.example.logisticapplication.domain.Order.*;
import org.example.logisticapplication.domain.RoutePoint.RoutePointForOrderDto;
import org.example.logisticapplication.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
//    private final OrderMapper orderMapper;

    @PostMapping("/create-order")
    public ResponseEntity<OrderInfo> createOrder(
            @RequestBody CreateBaseOrder baseOrder,
            @RequestParam("truckId") Long truckId,
            @RequestParam("driversId") Set<Long> driversId
    ) {
        log.info("Get request for creating order: {}", baseOrder);
        var newOrder = orderService.createBaseOrder(baseOrder, truckId, driversId);

        return ResponseEntity.ok()
                .body(newOrder);
    }

//    @PostMapping("/{id}")
//    public ResponseEntity<OrderDto> addRoutePoints(
//            @PathVariable("id") Long orderId
//    ) {
//        return null;
//    }

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
}
