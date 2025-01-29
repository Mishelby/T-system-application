package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoForOrderDto;
import org.example.logisticapplication.domain.Cargo.CargoInfoDto;
import org.example.logisticapplication.domain.Cargo.CargoStatus;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.example.logisticapplication.domain.Driver.DriverAllInfoDto;
import org.example.logisticapplication.domain.Driver.DriverDefaultValues;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriversAndTrucksForOrderDto;
import org.example.logisticapplication.domain.Order.*;
import org.example.logisticapplication.domain.OrderDistanceEntity;
import org.example.logisticapplication.domain.RoutePoint.*;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.Truck.TruckStatus;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.mapper.*;
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
    private final DistanceRepository distanceRepository;
    private final OrderDistanceRepository orderDistanceRepository;

    @Transactional
    public BaseOrderInfo createBaseOrder(
            CreateBaseOrder createBaseOrder
    ) {
        var countryMapEntity = getCountryMapEntity(createBaseOrder);
        var orderEntity = createNewOrder(countryMapEntity);
        var routePointEntities = saveRoutePoints(createBaseOrder.routePointInfoDto(), orderEntity);

        var distances = getDistanceEntity(routePointEntities);

        setRoutePointsForOrder(orderEntity, routePointEntities);
        var updatedOrder = orderRepository.save(orderEntity);
        var routePointInfoDto = getRoutePointInfoDto(routePointEntities, distances);

        saveDistanceForOrder(orderEntity, distances);

        return orderMapper.toBaseOrderInfo(
                updatedOrder,
                routePointInfoDto
        );
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

    @Transactional(readOnly = true)
    public List<OrderInfo> gerOrdersForSubmit() {
        var ordersForSubmit = orderRepository.findOrdersForSubmit();

        if (ordersForSubmit.isEmpty()) {
            return Collections.emptyList();
        }

        return ordersForSubmit.stream()
                .map(order -> {
                    var distanceEntity = getDistanceEntity(order);
                    var routePointInfoDto = getRoutePointInfoDto(order.getRoutePoints(), distanceEntity);

                    return orderMapper.toOrderInfo(order, routePointInfoDto);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderStatusDto getOrderStatusById(
            Long orderId
    ) {
        orderValidHelper.validateOrderAndFetch(orderId);

        return orderRepository.showOrderStatusByOrderId(orderId);
    }

    @Transactional
    public CreateBaseOrder sendBaseOrder(
            BaseRoutePoints routePoint
    ) {
        var routePointInfoDto = new ArrayList<RoutePointInfoDto>();
        addRoutePoints(routePoint, routePointInfoDto);

        saveDistance(
                routePoint.getCityFrom(),
                routePoint.getCityTo(),
                routePoint.getDistance()
        );

        return new CreateBaseOrder(routePointInfoDto);
    }

    private DistanceEntity getDistanceEntity(
            OrderEntity orderEntity
    ) {
      return   orderDistanceRepository.findDistanceEntityByOrder(orderEntity.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Distance entity for order with id = %s not found"
                                .formatted(orderEntity.getId())
                ));
    }

    private DistanceEntity getDistanceEntity(
            Set<RoutePointEntity> routePointEntities
    ) {
        var cityEntityFrom = routePointEntities.stream()
                .filter(rp -> OperationType.LOADING.name().equals(rp.getOperationType()))
                .map(RoutePointEntity::getCity)
                .toList().getFirst();

        var cityEntityTo = routePointEntities.stream()
                .filter(rp -> OperationType.UNLOADING.name().equals(rp.getOperationType()))
                .map(RoutePointEntity::getCity)
                .toList().getFirst();

        return cityEntityFrom.getCountryMap().getDistances()
                .stream()
                .filter(distanceEntity ->
                        distanceEntity.getFromCity().getId().equals(cityEntityFrom.getId()) &&
                                distanceEntity.getToCity().getId().equals(cityEntityTo.getId()))
                .findFirst()
                .orElseThrow();
    }

    private List<RoutePointInfoDto> getRoutePointInfoDto(
            Set<RoutePointEntity> routePointEntities,
            DistanceEntity distanceEntity
    ) {
        return routePointEntities.stream()
                .map(rp -> {
                    var cargoList = rp.getCargo().stream()
                            .map(cargoMapper::toDtoInfo)
                            .collect(Collectors.toSet());

                    return routePointMapper.toInfoDto(rp, cargoList, distanceEntity.getDistance());
                }).toList();
    }

    private List<RoutePointInfoDto> getRoutePointInfoDto(
            Set<RoutePointEntity> routePointEntities
    ) {
        return routePointEntities.stream()
                .map(rp -> {
                    var cargoList = rp.getCargo().stream()
                            .map(cargoMapper::toDtoInfo)
                            .collect(Collectors.toSet());

                    return routePointMapper.toInfoDto(rp, cargoList);
                }).toList();
    }

    private static void setRoutePointsForOrder(
            OrderEntity orderEntity,
            Set<RoutePointEntity> routePointEntities
    ) {
        orderEntity.setRoutePoints(routePointEntities);
        orderEntity.setDriverOrders(null);
        orderEntity.setTruckOrders(null);
    }

    public void saveDistanceForOrder(
            OrderEntity order,
            DistanceEntity distanceEntity
    ) {
        var orderDistanceEntity = new OrderDistanceEntity(
                order,
                distanceEntity
        );

        orderDistanceRepository.save(orderDistanceEntity);
    }


    public void saveDistance(
            String cityFrom,
            String cityTo,
            Long distance
    ) {
        var cityEntityFrom = cityRepository.findCityEntityByName(cityFrom).orElseThrow();
        var cityEntityTo = cityRepository.findCityEntityByName(cityTo).orElseThrow();
        var countryMapEntity = countryMapRepository.findByCityName(cityFrom).orElseThrow();

        if (!distanceRepository.isExistsDistanceEntity(
                List.of(cityEntityFrom.getId(), cityEntityTo.getId()),
                distance)
        ) {
            var distanceEntity = new DistanceEntity(
                    cityEntityFrom,
                    cityEntityTo,
                    distance,
                    countryMapEntity
            );

            distanceRepository.save(distanceEntity);
        }

    }

    private void addRoutePoints(
            BaseRoutePoints routePoint,
            List<RoutePointInfoDto> routePointInfoDto
    ) {
        var cargoNumber = CargoGenerateInfo.generateCargoNumber();
        var cargoName = CargoGenerateInfo.generateCargoName();

        var cargoInfoDto = getCargoInfoDto(
                new BigDecimal(routePoint.getCargoDto().getWeightKg()),
                cargoNumber,
                cargoName
        );

        addRoutePointsToList(
                routePoint.getCityFrom(),
                routePoint.getCityTo(),
                routePointInfoDto,
                cargoInfoDto,
                routePoint.getDistance()
        );
    }

    private static void addRoutePointsToList(
            String cityFrom,
            String cityTo,
            List<RoutePointInfoDto> routePointInfoDto,
            CargoInfoDto cargoInfoDto,
            Long distance
    ) {
        routePointInfoDto.add(
                new RoutePointInfoDto(
                        cityFrom,
                        Set.of(cargoInfoDto),
                        OperationType.LOADING.name(),
                        distance
                )
        );

        routePointInfoDto.add(
                new RoutePointInfoDto(
                        cityTo,
                        Set.of(cargoInfoDto),
                        OperationType.UNLOADING.name(),
                        distance
                )
        );
    }

    private CargoInfoDto getCargoInfoDto(
            BigDecimal weightKg,
            String cargoNumber,
            String cargoName
    ) {
        return cargoMapper.toInfoDto(
                weightKg,
                cargoNumber,
                cargoName,
                CargoStatus.NOT_SHIPPED.name()
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

    private CountryMapEntity getCountryMapEntity(
            CreateBaseOrder createBaseOrder
    ) {
        return createBaseOrder.routePointInfoDto()
                .stream()
                .filter(rp -> rp.getOperationType().equals(OperationType.LOADING.name()))
                .findFirst()
                .map(RoutePointInfoDto::getCityName)
                .flatMap(countryMapRepository::findByCityName)
                .orElseThrow(() -> new EntityNotFoundException("Country map for city not found"));
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
                .orElseThrow(
                        () -> new EntityNotFoundException("Truck with id = %s not found".formatted(truckId))
                );
    }

    private OrderEntity createNewOrder(
            CountryMapEntity countryMapEntity
    ) {
        var uniqueNumber = orderValidHelper.generateUniqueNumber();

        var orderEntity = new OrderEntity(
                uniqueNumber,
                OrderStatus.NOT_COMPLETED.getName(),
                countryMapEntity
        );

        return orderRepository.save(orderEntity);
    }

    public Set<RoutePointEntity> saveRoutePoints(
            List<RoutePointInfoDto> routePointInfoDto,
            OrderEntity orderEntity
    ) {
        return routePointInfoDto
                .stream()
                .map(rp -> {
                    var cargoEntityList = rp.getCargoInfo()
                            .stream()
                            .filter(cargo -> OperationType.LOADING.name().equals(rp.getOperationType()))
                            .map(cargoMapper::toEntity)
                            .toList();

                    var cityEntity = cityRepository.findCityEntityByName(rp.getCityName())
                            .orElseThrow(
                                    () -> new EntityNotFoundException("City with name = %s not found"
                                            .formatted(rp.getCityName()))
                            );

                    var routePoint = routePointMapper.toEntity(rp, cargoEntityList, cityEntity);
                    routePoint.setOrder(orderEntity);

                    return routePointRepository.save(routePoint);
                })
                .collect(Collectors.toSet());
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
                )
                .toList();
    }

    private static long getTotalDistance(
            List<RoutePointForOrderDto> routePointsDto
    ) {
        return routePointsDto.stream()
                .filter(rp -> OperationType.LOADING.toString().equals(rp.operationType()))
                .mapToLong(RoutePointForOrderDto::distance)
                .sum();
    }

    private static long getTotalWeight(
            List<RoutePointForOrderDto> routePointsDto
    ) {
        return routePointsDto.stream()
                .filter(rp -> OperationType.LOADING.toString().equals(rp.operationType()))
                .flatMap(rp -> rp.cargos().stream())
                .mapToLong(CargoForOrderDto::weight)
                .sum();
    }

    private CityEntity findCityEntityByName(
            String loadingCityName
    ) {
        return cityRepository.findCityEntityByName(loadingCityName)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No city for loading operation. City name: " + loadingCityName));
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

    public static Long calculationTimeToOrder(
            Long distance,
            Long averageSpeed
    ) {
        return Math.ceilDiv(distance, averageSpeed);
    }

}
