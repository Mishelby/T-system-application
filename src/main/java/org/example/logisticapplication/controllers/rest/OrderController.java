package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Driver.DriversAndTrucksForOrder;
import org.example.logisticapplication.domain.DriverOrderEntity.DriversAndTrucksForOrderDto;
import org.example.logisticapplication.domain.Order.*;
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
            @RequestBody OrderInfoForUserDto orderInfo
    ) {
        log.info("Get request for creating order: {}", orderInfo);
        var newOrder = orderService.createBaseOrder(orderInfo);

        return ResponseEntity.ok().body(newOrder);
    }

    @GetMapping("/pending-submit")
    public ResponseEntity<List<BaseOrderForSubmitDto>> getOrdersForSubmit(
            @RequestParam(value = "defaultValue", required = false) DefaultSubmittingSize defaultSubmittingSize
    ) {
        log.info("Get request for submitting order {}",defaultSubmittingSize);
        var ordersForSubmit = orderService.findOrdersForSubmit(defaultSubmittingSize);

        return ResponseEntity.ok().body(ordersForSubmit);
    }

    @GetMapping("/{orderNumber}/drivers-trucks")
    public ResponseEntity<DriversAndTrucksForOrder> getTruckAndDriversForOrder(
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


}
