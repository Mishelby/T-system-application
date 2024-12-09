package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckStatus;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.*;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderValidHelper orderValidHelper;
    private final CountryMapRepository countryMapRepository;
    private final TruckRepository truckRepository;
    private final TruckMapper truckMapper;

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

        var routePointEntities = orderValidHelper.getRoutePointEntities(orderRequest);
        var orderEntity = orderMapper.toEntity(orderRequest, countryMapEntity, routePointEntities);

        routePointEntities.forEach(entity -> entity.setOrder(orderEntity));
        orderRepository.save(orderEntity);

        return orderMapper.toDomain(orderEntity);
    }



    @Transactional
    public List<Truck> findTruckForOrder(
            String uniqueNumber
    ) {
        orderValidHelper.validateOrderAndFetch(uniqueNumber);
        var correctTrucks = truckRepository.findAllInCurrentCity(TruckStatus.SERVICEABLE.toString());

        return correctTrucks.stream()
                .map(truckMapper::toDomain)
                .toList();

    }
}
