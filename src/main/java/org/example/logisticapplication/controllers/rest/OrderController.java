package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Driver.DriverDto;
import org.example.logisticapplication.domain.DriverOrderEntity.DriversAndTrucksForOrderDto;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Order.OrderDto;
import org.example.logisticapplication.domain.Order.OrderStatusDto;
import org.example.logisticapplication.domain.RoutePoint.RoutePointDto;
import org.example.logisticapplication.domain.RoutePoint.RoutePointForOrderDto;
import org.example.logisticapplication.domain.Truck.TruckDto;
import org.example.logisticapplication.service.OrderService;
import org.example.logisticapplication.utils.DriverMapper;
import org.example.logisticapplication.utils.OrderMapper;
import org.example.logisticapplication.utils.TruckMapper;
import org.springframework.http.HttpStatus;
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
    private final OrderMapper orderMapper;

    @PostMapping("/create-order")
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody CreateOrderRequest order
    ) {
        log.info("Get request for creating order: {}", order);
        var newOrder = orderService.createBaseOrder(order);

        return ResponseEntity.ok(orderMapper.toDto(newOrder));
    }

    @PostMapping("/appoint-order")
    public ResponseEntity<OrderDto> updateOrder(
            @RequestParam("truckId") Long truckId,
            @RequestParam("driverId") Set<Long> driverId,
            @RequestBody List<RoutePointForOrderDto> routePoints
    ){
        log.info("Get request for updating order: {}, {}, {}", truckId, driverId, routePoints);
        var order = orderService.appointTruckAndDrivers(truckId, driverId, routePoints);

        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    @GetMapping("/trucks-drivers")
    public ResponseEntity<DriversAndTrucksForOrderDto> getTruckAndDriversForOrder(
            @RequestBody List<RoutePointForOrderDto> routePointDto
    ) {
        log.info("Get request for getting trucks for order");
        var truckForOrder = orderService.findTrucksAndDriversForOrder(routePointDto);

        return ResponseEntity.ok(truckForOrder);
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
