package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Driver.DriverAndTruckDto;
import org.example.logisticapplication.domain.Order.OrderInfoDto;
import org.example.logisticapplication.service.OrderInfoService;
import org.example.logisticapplication.utils.OrderMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderInfoController {
    private final OrderInfoService orderInfoService;
    private final OrderMapper orderMapper;

    @GetMapping("/info")
    public ResponseEntity<List<OrderInfoDto>> getOrderInfos(
            @RequestParam(value = "countOfLast") Integer countOfLastOrders
    ) {
        log.info("Get request for get last orders");
        var lastOrders = orderInfoService.findLastOrders(countOfLastOrders);

        return ResponseEntity.ok(
                lastOrders.stream()
                        .map(orderMapper::toDtoInfo)
                        .toList()
        );
    }

    @GetMapping("/drivers-trucks")
    public ResponseEntity<DriverAndTruckDto> getOrderInfoById() {
        log.info("Get request for get drivers and trucks");

        return ResponseEntity.ok(orderInfoService.findAllDriversAndTrucks());
    }
}
