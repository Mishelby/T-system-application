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

    @Transactional
    public OrderInfo createBaseOrder(
            CreateBaseOrder createBaseOrder,
            Long truckId,
            Set<Long> driversId
    ) {
        // Retrieve the CountryMapEntity for the loading city specified in the base order
        var countryMapEntity = getCountryMapEntity(createBaseOrder);

        // Find all driver entities by their IDs
        var allDriversById = driverRepository.findAllById(driversId);

        // Find the truck entity by its ID or throw an exception if not found
        var truckEntity = truckRepository.findById(truckId).orElseThrow(
                () -> new EntityNotFoundException("Truck with id = %s not found".formatted(truckId))
        );

        // Create a new order entity with the country map
        var orderEntity = createNewOrder(countryMapEntity);

        // Save route point entities associated with the order
        var routePointEntities = saveRoutePoints(createBaseOrder.routePointInfoDto(), orderEntity);

        // Create a set of truck order entities and driver order entities for the order
        var truckEntitySet = getTruckOrderEntities(truckId, orderEntity);
        var drivers = getDriverOrderEntities(allDriversById, orderEntity);

        // Set route points, driver orders, and truck orders on the order entity
        orderEntity.setRoutePoints(routePointEntities);
        orderEntity.setDriverOrders(drivers);
        orderEntity.setTruckOrders(truckEntitySet);

        // Save and update the order entity in the repository
        var updatedOrder = orderRepository.save(orderEntity);

        // Return OrderInfo with details about the created order
        return orderMapper.toDomainInfo(
                updatedOrder,
                createBaseOrder.routePointInfoDto(),
                driverMapper.toOrderInfo(allDriversById),
                truckMapper.toInfoDto(List.of(truckEntity))
        );
    }


    @Transactional(readOnly = true)
    public DriversAndTrucksForOrderDto findTrucksAndDriversForOrder(
            List<RoutePointForOrderDto> routePointsDto
    ) {
        // Find loading city name
        var loadingCityName = findLoadingCityName(routePointsDto);

        // Find loading city entity by name
        var loadingCityEntity = findCityEntityByName(loadingCityName);

        // Calculate total weight of all route points
        long totalWeight = getTotalWeight(routePointsDto);

        // Find trucks for order by weight
        var trucksForOrderByWeight = truckRepository.findTrucksForOrderByWeight(
                loadingCityEntity.getId(),
                TruckStatus.SERVICEABLE.toString(),
                new BigDecimal(totalWeight)
        );

        // Get all truck id's for mapping
        var trucksId = trucksForOrderByWeight
                .stream()
                .map(truckMapper::toDomain)
                .map(Truck::currentCityId)
                .collect(Collectors.toSet());

        // Calculate total distance of all route points
        long totalDistance = getTotalDistance(routePointsDto);

        // Find drivers for order
        var driversForOrder = driverRepository.findDriversByTruckId(
                trucksId,
                totalDistance,
                defaultValues.getAverageSpeed(),
                defaultValues.getAverageSpeed()
        );

        // Map drivers to domain info
        var driverAllInfo = mapDriversToDto(driversForOrder);

        // Map trucks to domain info
        var truckInfoDto = mapTrucksToDto(trucksForOrderByWeight);

        // Return DriversAndTrucksForOrderDto with drivers and trucks for order
        return orderMapper.toDtoInfo(
                driverAllInfo,
                truckInfoDto
        );
    }

    @Transactional(readOnly = true)
    public OrderStatusDto getOrderStatusById(
            Long orderId
    ) {
        // Validate order and fetch it
        orderValidHelper.validateOrderAndFetch(orderId);

        // Return order status by order id
        return orderRepository.showOrderStatusByOrderId(orderId);
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
        // Stream through route points to find the first loading operation
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

    private static Set<DriverOrderEntity> getDriverOrderEntities(
            List<DriverEntity> allDriversById,
            OrderEntity orderEntity
    ) {
        // Create a new DriverOrderEntity for each driver in the list and add it to the set
        return allDriversById
                .stream()
                .map(driverEntity -> new DriverOrderEntity(orderEntity, driverEntity))
                .collect(Collectors.toSet());
    }

    private OrderEntity createNewOrder(
            CountryMapEntity countryMapEntity
    ) {
        // Generate a unique number for the order
        var uniqueNumber = orderValidHelper.generateUniqueNumber();

        // Create a new order entity with NOT_COMPLETED status and the given country map
        var orderEntity = new OrderEntity(
                uniqueNumber,
                OrderStatus.NOT_COMPLETED.getName(),
                countryMapEntity
        );

        // Save the order and return the created entity
        return orderRepository.save(orderEntity);
    }

    public Set<RoutePointEntity> saveRoutePoints(
            List<RoutePointInfoDto> routePointInfoDto,
            OrderEntity orderEntity
    ) {
        // Create a set to store the route point entities
        return routePointInfoDto
                .stream()
                .map(rp -> {
                    // Get the cargo entities for the route point
                    var cargoEntityList = rp.getCargoInfo()
                            .stream()
                            .map(cargoMapper::toEntity)
                            .toList();

                    // Get the city entity for the route point
                    var cityEntity = cityRepository.findCityEntityByName(rp.getCityName())
                            .orElseThrow(
                                    () -> new EntityNotFoundException("City with name = %s not found"
                                            .formatted(rp.getCityName()))
                            );

                    // Create a new route point entity and set its order
                    var routePoint = routePointMapper.toEntity(rp, cargoEntityList, cityEntity);
                    routePoint.setOrder(orderEntity);

                    // Save the route point entity
                    return routePointRepository.save(routePoint);
                })
                .collect(Collectors.toSet());
    }

    private List<DriverAllInfoDto> mapDriversToDto(
            List<DriverEntity> driversForOrder
    ) {
        return driversForOrder.stream()
                // Map each driver entity to a driver all info dto
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
        // Stream through the route points and then the cargoes of each route point
        return routePointsDto.stream()
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
        // Stream through the route points and filter out the loading operations
        return routePointsDto.stream()
                .filter(rp -> rp.operationType().equals(OperationType.LOADING.toString()))
                .map(RoutePointForOrderDto::cityName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No loading operation found"));
    }

    public static Long calculationTimeToOrder(
            Long distance,
            Long averageSpeed
    ){
      return Math.ceilDiv(distance, averageSpeed);
    }
}
