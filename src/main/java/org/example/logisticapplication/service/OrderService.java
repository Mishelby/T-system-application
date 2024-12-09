package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckStatus;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


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

        var routePointEntities = orderRequest.routePoints()
                .stream()
                .map(routePoint -> {
                    var cityEntity = cityRepository.findById(routePoint.cityId()).orElseThrow(
                            () -> new EntityNotFoundException(
                                    "City Map with id=%s Not Found"
                                            .formatted(routePoint.cityId())
                            )
                    );

                    var cargoEntity = cargoRepository.findAllById(routePoint.cargoId());

                    //TODO: check if cargo is in correct city
                    if(cargoEntity.size() != routePoint.cargoId().size()) {
                        throw new EntityNotFoundException(
                                "Cargo with id=%s Not Found"
                                        .formatted(routePoint.cargoId())
                        );
                    }

                    return routePointMapper.toEntity(routePoint, cityEntity, cargoEntity);
                })
                .toList();

        var orderEntity = orderMapper.toEntity(orderRequest, countryMapEntity, routePointEntities);

        routePointEntities.forEach(entity -> entity.setOrder(orderEntity));

        orderRepository.save(orderEntity);

        return orderMapper.toDomain(orderEntity);
    }


    @Transactional
    public List<Truck> findTruckForOrder(
            String uniqueNumber
    ) {
        validateOrderAndFetch(uniqueNumber);
        var correctTrucks = truckRepository.findAllInCurrentCity(TruckStatus.SERVICEABLE.toString());

        return correctTrucks.stream()
                .map(truckMapper::toDomain)
                .toList();

    }

    @Transactional(readOnly = true)
    public void validateOrderAndFetch(String uniqueNumber) {
        if (!orderRepository.existsByUniqueNumber(uniqueNumber)) {
            throw new EntityNotFoundException(
                    "Order with unique number=%s not Found"
                            .formatted(uniqueNumber)
            );
        }

    }
}
