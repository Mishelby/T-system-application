package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrder;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderValidHelper orderValidHelper;
    private final CountryMapRepository countryMapRepository;
    private final RoutePointMapper routePointMapper;
    private final CityRepository cityRepository;
    private final CargoRepository cargoRepository;

    @Transactional
    public Order createBaseOrder(
            CreateOrderRequest orderRequest
    ) {
        orderValidHelper.isOrderHasBeenCreated(orderRequest);

        var countryMapEntity = countryMapRepository.findById(orderRequest.countyMapId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Country Map with id=%s Not Found"
                                .formatted(orderRequest.countyMapId())
                )
        );

        var routePointEntities = orderRequest.routePoints()
                .stream()
                .map(routePoint -> {
                    var cityEntity = cityRepository.findById(routePoint.cityId()).orElseThrow(
                            () -> new EntityNotFoundException(
                                    "City Map with id=%s Not Found"
                                            .formatted(routePoint.cityId())
                            )
                    );

                    var cargoEntity = cargoRepository.findById(routePoint.cargoId()).orElseThrow(
                            () -> new EntityNotFoundException(
                                    "Cargo Map with id=%s Not Found"
                                            .formatted(routePoint.cargoId())
                            )
                    );

                    return routePointMapper.toEntity(routePoint, cityEntity, cargoEntity);
                })
                .toList();

        var orderEntity = orderMapper.toEntity(orderRequest, countryMapEntity, routePointEntities);

        orderRepository.save(orderEntity);

        return orderMapper.toDomain(orderEntity);
    }


}
