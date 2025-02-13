package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.controllers.mvc.OrderForSubmittingDto;
import org.example.logisticapplication.domain.DriverOrderEntity.DriversAndTrucksForOrderDto;
import org.example.logisticapplication.domain.Order.*;
import org.example.logisticapplication.domain.RoutePoint.BaseRoutePoints;
import org.example.logisticapplication.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<BaseOrderInfo> createOrder(
            @RequestBody CreateBaseOrder baseOrder
    ) {
        log.info("Get request for creating order: {}", baseOrder);
        var newOrder = orderService.createBaseOrder(baseOrder);

        return ResponseEntity.ok().body(newOrder);
    }

    @PostMapping("/confirm")
    public ResponseEntity<CreateBaseOrder> confirmOrder(
            @RequestBody BaseRoutePoints routePoint
    ) {
        log.info("Get request for confirming order: {}", routePoint);
        var createBaseOrder = orderService.sendBaseOrder(routePoint);

        return ResponseEntity.ok().body(createBaseOrder);
    }


    @GetMapping("/{orderNumber}/drivers-trucks")
    public ResponseEntity<DriversAndTrucksForOrderDto> getTruckAndDriversForOrder(
            @PathVariable("orderNumber") String orderNumber
    ) {
        log.info("Get request for getting trucks for order");
        var truckForOrder = orderService.findTrucksAndDriversForOrder(orderNumber);

        return ResponseEntity.ok().body(truckForOrder);
    }

    @PostMapping("/{orderNumber}/finalize")
    public ResponseEntity<Void> applyForOrder(
            @PathVariable("orderNumber") String orderNumber,
            ApplyOrderDto applyOrderDto
    ) {
        log.info("Post request for applying drivers and truck for order: {}", orderNumber);
        orderService.applyForOrder(orderNumber, applyOrderDto);

        return ResponseEntity.accepted().build();
    }


    @GetMapping("/{id}/status")
    public ResponseEntity<OrderStatusDto> getOrderStatusById(
            @PathVariable("id") Long orderId
    ) {
        log.info("Get request for getting status for order");
        var orderStatusById = orderService.getOrderStatusById(orderId);

        return ResponseEntity.ok().body(orderStatusById);
    }

    @PostMapping("/send-for-submitting")
    public ResponseEntity<OrderForSubmittingDto> sendOrderForSubmitting(
            @RequestParam  SendOrderForSubmittingDto orderDto
    ) {
        log.info("Get request for submitting order {}",orderDto);
        orderService.responseOrderForSubmitting(orderDto);

        return ResponseEntity.ok(null);
    }

    @GetMapping("/pending-submit")
    public ResponseEntity<List<OrderInfo>> getOrdersForSubmit(
            @RequestParam(value = "defaultValue", required = false) DefaultSubmittingSize defaultSubmittingSize
    ) {
        log.info("Get request for submitting orders");
        var orderInfo = orderService.gerOrdersForSubmit(defaultSubmittingSize);

        return ResponseEntity.ok().body(orderInfo);
    }

}
