package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoForOrderDto;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
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

    @Transactional
    public OrderInfo createBaseOrder(
            CreateBaseOrder createBaseOrder,
            Long truckId,
            Set<Long> driversId
    ) {
        /**
         * Find country map by loading city
         */
        var countryMapEntity = getCountryMapEntity(createBaseOrder);

        var allDriversById = driverRepository.findAllById(driversId);
        var truckEntity = truckRepository.findById(truckId).orElseThrow();

        var orderEntity = createNewOrder(countryMapEntity);

        /**
         * Mapping to entities and saving route points in database
         */
        var routePointEntities = saveRoutePoints(createBaseOrder.routePointInfoDto(), orderEntity);

        /**
         * Mapping to entities and return
         * new HashSet<TruckOrderEntity> and
         * new HashSet<DriverOrderEntity>
         */
        var truckEntitySet = getTruckOrderEntities(truckId, orderEntity);
        var drivers = getDriverOrderEntities(allDriversById, orderEntity);

        orderEntity.setRoutePoints(routePointEntities);
        orderEntity.setDriverOrders(drivers);
        orderEntity.setTruckOrders(truckEntitySet);

        var updatedOrder = orderRepository.save(orderEntity);

        return orderMapper.toDomainInfo(
                updatedOrder,
                createBaseOrder.routePointInfoDto(),
                driverMapper.toOrderInfo(allDriversById),
                truckMapper.toInfoDto(List.of(truckEntity))
        );
    }

    private CountryMapEntity getCountryMapEntity(
            CreateBaseOrder createBaseOrder
    ) {
        return createBaseOrder.routePointInfoDto()
                .stream()
                .filter(rp -> rp.operationType().equals(OperationType.LOADING.name()))
                .findFirst()
                .map(RoutePointInfoDto::cityName)
                .flatMap(countryMapRepository::findByCityName).orElseThrow(
                        () -> new EntityNotFoundException("Country map for city not found")
                );
    }

    private Set<TruckOrderEntity> getTruckOrderEntities(
            Long truckId,
            OrderEntity orderEntity
    ) {
        return truckRepository.findById(truckId)
                .map(truck -> new TruckOrderEntity(orderEntity, truck))
                .map(truck -> {
                    Set<TruckOrderEntity> set = new HashSet<>();
                    set.add(truck);
                    return set;
                })
                .orElseThrow();
    }

    private static Set<DriverOrderEntity> getDriverOrderEntities(
            List<DriverEntity> allDriversById,
            OrderEntity orderEntity
    ) {
        return allDriversById
                .stream()
                .map(driverEntity -> new DriverOrderEntity(orderEntity, driverEntity))
                .collect(Collectors.toSet());
    }

    private OrderEntity createNewOrder(
            CountryMapEntity countryMapEntity
    ) {
        var orderEntity = new OrderEntity();
        orderEntity.setUniqueNumber(orderValidHelper.generateUniqueNumber());
        orderEntity.setStatus(OrderStatus.NOT_COMPLETED.getName());
        orderEntity.setCountryMap(countryMapEntity);
        return orderRepository.save(orderEntity);
    }

    public Set<RoutePointEntity> saveRoutePoints(
            List<RoutePointInfoDto> routePointInfoDto,
            OrderEntity orderEntity
    ) {
        var routePointEntities = routePointInfoDto
                .stream()
                .map(rp -> {
                    var cargoEntityList = rp.cargoInfo()
                            .stream()
                            .map(cargoMapper::toEntity)
                            .toList();

                    var cityEntity = cityRepository.findCityEntityByName(rp.cityName())
                            .orElseThrow(
                                    () -> new EntityNotFoundException("City with name = %s not found"
                                            .formatted(rp.cityName()))
                            );

                    return routePointMapper.toEntity(rp, cargoEntityList, cityEntity);
                })
                .collect(Collectors.toSet());

        routePointEntities.forEach(routePoint -> {
            routePoint.setOrder(orderEntity);
            routePointRepository.save(routePoint);
        });

        return routePointEntities;
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

    private List<TruckInfoDto> mapTrucksToDto(
            List<TruckEntity> trucksForOrderByWeight
    ) {
        return trucksForOrderByWeight
                .stream()
                .map(truckMapper::toInfoDto)
                .toList();
    }

    private List<DriverAllInfoDto> mapDriversToDto(
            List<DriverEntity> driversForOrder
    ) {
        return driversForOrder.stream()
                .map(driver ->
                        driverMapper.toDtoInfo(
                                driver,
                                cityMapper.toInfoDto(driver.getCurrentCity()),
                                truckMapper.toInfoDto(driver.getCurrentTruck())
                        )
                ).toList();
    }

    private static long getTotalDistance(
            List<RoutePointForOrderDto> routePointsDto
    ) {
        return routePointsDto
                .stream()
                .filter(rp -> rp.operationType().equals(OperationType.LOADING.toString()))
                .map(RoutePointForOrderDto::distance)
                .mapToLong(num -> num)
                .sum();
    }

    private static long getTotalWeight(
            List<RoutePointForOrderDto> routePointsDto
    ) {
        return routePointsDto.stream()
                .flatMap(rp -> rp.cargos().stream())
                .mapToLong(CargoForOrderDto::weight)
                .sum();
    }

    private CityEntity findCityEntityByName(
            String loadingCityName
    ) {
        return cityRepository.findCityEntityByName(loadingCityName)
                .orElseThrow(() -> new EntityNotFoundException("No city for loading operation"));
    }

    private static String findLoadingCityName(
            List<RoutePointForOrderDto> routePointsDto
    ) {
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
}
