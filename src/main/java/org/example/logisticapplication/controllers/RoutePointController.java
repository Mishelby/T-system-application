package org.example.logisticapplication.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointDto;
import org.example.logisticapplication.service.RoutePointService;
import org.example.logisticapplication.utils.RoutePointMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/route-points")
@RequiredArgsConstructor
@Slf4j
public class RoutePointController {
    private final RoutePointService routePointService;
    private final RoutePointMapper routePointMapper;

    @PostMapping
    public ResponseEntity<RoutePointDto> save(
            @RequestBody RoutePoint routePoint
    ) {
        log.info("Get request for saving route point: {}", routePoint);
        var domain = routePointService.addNewRoutePoint(routePoint);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        routePointMapper.toDto(domain)
                );
    }
}
