package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoForOrderDto;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverAllInfoDto;
import org.example.logisticapplication.domain.Driver.DriverDefaultValues;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriversAndTrucksForOrderDto;
import org.example.logisticapplication.domain.Order.*;
import org.example.logisticapplication.domain.RoutePoint.*;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.Truck.TruckStatus;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderValidHelper orderValidHelper;
    private final CountryMapRepository countryMapRepository;
    private final TruckRepository truckRepository;
    private final TruckMapper truckMapper;
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final RoutePointRepository routePointRepository;
    private final DriverDefaultValues defaultValues;
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final RoutePointMapper routePointMapper;
    private final CargoMapper cargoMapper;
    private final CargoRepository cargoRepository;

    @Transactional
    public Order createBaseOrder(
            CreateBaseOrder createBaseOrder
    ) {
//        orderValidHelper.isOrderHasBeenCreated(createBaseOrder);

        var baseOrder = new BaseOrder(
                orderValidHelper.generateUniqueNumber(),
                OrderStatus.NOT_COMPLETED.getName(),
                createBaseOrder.routePointInfoDto()
        );

        var routePointEntities = baseOrder.routePoints()
                .stream()
                .map(rp -> {
                    var cargoEntities = rp.cargoInfo()
                            .stream()
                            .map(cargoMapper::toEntity)
                            .toList();

                    var cityEntity = cityRepository.findCityEntityByName(rp.cityName()).orElseThrow(
                            () -> new EntityNotFoundException("City with name = %s not found"
                                    .formatted(rp.cityName()))
                    );

                    return routePointMapper.toEntity(rp, cargoEntities, cityEntity);
                }).peek(routePointRepository::save)
                .collect(Collectors.toSet());

        var cityName = createBaseOrder.routePointInfoDto()
                .stream()
                .filter(rp -> rp.operationType().equalsIgnoreCase(OperationType.LOADING.name()))
                .map(RoutePointInfoDto::cityName)
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("City with loading operation type not found!")
                );

        var countryMapEntity = countryMapRepository.findByCityName(cityName).orElseThrow(
                () -> new EntityNotFoundException(
                        "Country map for city with name = %s not found"
                        .formatted(cityName)
                )
        );

        var orderEntity = orderRepository.save(
                orderMapper.toEntity(
                        baseOrder,
                        routePointEntities,
                        countryMapEntity
                )
        );

        return orderMapper.toDomain(orderEntity);
    }

    @Transactional(readOnly = true)
    public DriversAndTrucksForOrderDto findTrucksAndDriversForOrder(
            List<RoutePointForOrderDto> routePointsDto
    ) {

        var loadingCityName = findLoadingCityName(routePointsDto);
        var loadingCityEntity = findCityEntityByName(loadingCityName);

        long totalWeight = getTotalWeight(routePointsDto);

        var trucksForOrderByWeight = truckRepository.findTrucksForOrderByWeight(
                loadingCityEntity.getId(),
                TruckStatus.SERVICEABLE.toString(),
                new BigDecimal(totalWeight)
        );

        var trucksId = trucksForOrderByWeight
                .stream()
                .map(truckMapper::toDomain)
                .map(Truck::currentCityId)
                .collect(Collectors.toSet());

        long totalDistance = getTotalDistance(routePointsDto);

        var driversForOrder = driverRepository.findDriversByTruckId(
                trucksId,
                totalDistance,
                defaultValues.getAverageSpeed(),
                defaultValues.getAverageSpeed()
        );

        var driverAllInfo = mapDriversToDto(driversForOrder);
        var truckInfoDto = mapTrucksToDto(trucksForOrderByWeight);


        return orderMapper.toDtoInfo(
                driverAllInfo,
                truckInfoDto
        );
    }

    private List<TruckInfoDto> mapTrucksToDto(List<TruckEntity> trucksForOrderByWeight) {
        return trucksForOrderByWeight
                .stream()
                .map(truckMapper::toInfoDto)
                .toList();
    }

    private List<DriverAllInfoDto> mapDriversToDto(List<DriverEntity> driversForOrder) {
        return driversForOrder.stream()
                .map(driver ->
                        driverMapper.toDtoInfo(
                                driver,
                                cityMapper.toInfoDto(driver.getCurrentCity()),
                                truckMapper.toInfoDto(driver.getCurrentTruck())
                        )
                ).toList();
    }

    private static long getTotalDistance(List<RoutePointForOrderDto> routePointsDto) {
        return routePointsDto
                .stream()
                .filter(rp -> rp.operationType().equals(OperationType.LOADING.toString()))
                .map(RoutePointForOrderDto::distance)
                .mapToLong(num -> num)
                .sum();
    }

    private static long getTotalWeight(List<RoutePointForOrderDto> routePointsDto) {
        return routePointsDto.stream()
                .flatMap(rp -> rp.cargos().stream())
                .mapToLong(CargoForOrderDto::weight)
                .sum();
    }

    private CityEntity findCityEntityByName(String loadingCityName) {
        return cityRepository.findCityEntityByName(loadingCityName)
                .orElseThrow(() -> new EntityNotFoundException("No city for loading operation"));
    }

    private static String findLoadingCityName(List<RoutePointForOrderDto> routePointsDto) {
        return routePointsDto.stream()
                .filter(rp -> rp.operationType().equals(OperationType.LOADING.toString()))
                .map(RoutePointForOrderDto::cityName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No loading operation found"));
    }

    @Transactional(readOnly = true)
    public OrderStatusDto getOrderStatusById(
            Long orderId
    ) {
        orderValidHelper.validateOrderAndFetch(orderId);

        return orderRepository.showOrderStatusByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public List<Driver> findDriversForOrder(
            Long orderId
    ) {
        orderValidHelper.validateOrderAndFetch(orderId);

        var routePointEntity = routePointRepository.findRoutePointEntitiesByOrderId(
                orderId,
                OperationType.LOADING.toString()
        );

        var driversForCorrectTruck = driverRepository.findDriversForCorrectTruck(
                routePointEntity.getCity().getId(),
                orderId,
                defaultValues.getAverageSpeed(),
                defaultValues.getNumberOfHoursWorkedLimit()
        );

        return driversForCorrectTruck
                .stream()
                .map(driverMapper::toDomain)
                .toList();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Order appointTruckAndDrivers(
            Long truckId,
            Set<Long> driversId,
            List<RoutePointForOrderDto> routePointDto
    ) {

        var truckEntities = Set.of(truckRepository.findById(truckId).orElseThrow());
        var driversEntityById = driverRepository.findAllDriversById(driversId);

        var cityName = routePointDto
                .stream()
                .filter(rp -> rp.operationType().equals(OperationType.LOADING.toString()))
                .map(RoutePointForOrderDto::cityName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No loading operation found"));

        var countryMapEntity = countryMapRepository.findByCityName(cityName).orElseThrow(
                () -> new EntityNotFoundException("City with name = %s not found!"
                        .formatted(cityName))
        );

        var routePointEntityList = routePointDto.stream()
                .map(routePoint -> {
                    var cityEntityByName = cityRepository.findCityEntityByName(routePoint.cityName()).orElseThrow(
                            () -> new EntityNotFoundException("City with name = %s not found!"
                                    .formatted(routePoint.cityName()))
                    );

                    var cargoEntities = routePoint.cargos().stream()
                            .map(cargo ->
                                    cargoMapper.toEntity(cargo, CargoNumberGenerator.generateNumber()))
                            .toList();

                    return routePointMapper.toEntity(routePoint, cityEntityByName, cargoEntities);
                }).peek(routePoint -> routePoint.setOrder(null))
                .collect(Collectors.toSet());


        var driverOrderEntity = driversEntityById.stream()
                .map(driverEntity -> new DriverOrderEntity(null, driverEntity))
                .collect(Collectors.toSet());

        var truckOrderEntity = truckEntities.stream()
                .map(truck -> new TruckOrderEntity(null, truck))
                .collect(Collectors.toSet());

        var orderEntity = orderMapper.toEntity(
                orderValidHelper.generateUniqueNumber(),
                OrderStatus.NOT_COMPLETED.name(),
                countryMapEntity,
                routePointEntityList,
                driverOrderEntity,
                truckOrderEntity
        );

        driverOrderEntity.forEach(driver -> driver.setOrder(orderEntity));
        truckOrderEntity.forEach(truck -> truck.setOrder(orderEntity));
        orderEntity.getRoutePoints().forEach(routePoint -> routePoint.setOrder(orderEntity));

        orderEntity.setStatus("NOT COMPLETED");

        var savedOrderEntity = orderRepository.save(orderEntity);

        return orderMapper.toDomain(savedOrderEntity);
    }
}
