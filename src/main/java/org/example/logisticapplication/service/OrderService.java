package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.OrderMapper;
import org.example.logisticapplication.utils.OrderValidHelper;
import org.example.logisticapplication.utils.RoutePointMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderValidHelper orderValidHelper;
    private final DriverRepository driverRepository;
    private final RoutePointMapper routePointMapper;
    private final CargoRepository cargoRepository;


//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
//    public Order createOrder(
//            Order order
//    ) {
//        orderValidHelper.isOrderHasBeenCreated(order);
//        orderValidHelper.isRoutePointsListEmpty(order);
//
//        var truckEntity = orderValidHelper.findTruckEntityById(order);
//        var allDriversById = driverRepository.findAllById(order.driversId());
//        var orderEntity = orderMapper.toEntity(order, allDriversById, truckEntity);
//
//        orderValidHelper.checkDriversById(allDriversById, order);
//
//        var listRoutePoints = order.routePoints()
//                .stream()
//                .map(routePointMapper::toEntity)
//                .peek(routePointEntity -> {
//                    var routePoints = order.routePoints()
//                            .stream()
//                            .map(RoutePoint::cargoId)
//                            .toList();
//
//                    var allCargoEntityById = cargoRepository.findCargoEntityByCorrectStatus(orderEntity.getId());
//                    allCargoEntityById.forEach(routePointEntity::setCargo);
//                }).toList();
//
//
//        orderEntity.setRoutePoints(listRoutePoints);
//        orderEntity.setDrivers(allDriversById);
//
//        var savedOrder = orderRepository.save(orderEntity);
//
//        return orderMapper.toDomain(savedOrder);
//    }


}
