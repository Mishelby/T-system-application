package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
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

    /**
     * Creates a new order with the provided base information, truck, and drivers.
     *
     * @param createBaseOrder The base information for the order.
     * @param truckId         The ID of the truck to associate with the order.
     * @param driversId       A set of driver IDs to associate with the order.
     * @return An OrderInfo object containing information about the created order.
     */
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

        setNewWorkingHours(createBaseOrder, allDriversById);


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


    /**
     * Calculate the total number of hours worked by the drivers for the order and update the driver entities.
     *
     * @param createBaseOrder The base information for the order.
     * @param driverEntities  The list of driver entities associated with the order.
     */
    private void setNewWorkingHours(
            CreateBaseOrder createBaseOrder,
            List<DriverEntity> driverEntities
    ) {
        // Sum up distances of all route points
        var sumOfHours = createBaseOrder.routePointInfoDto()
                .stream()
                .map(RoutePointInfoDto::distance)
                .reduce(0, Integer::sum);

        // Update the drivers with the calculated number of hours worked
        driverEntities.forEach(driver -> driver.setNumberOfHoursWorked(
                (int) (sumOfHours / defaultValues.getAverageSpeed()))
        );
    }

    /**
     * Method for finding trucks and drivers for order
     *
     * @param routePointsDto route points for order
     * @return DriversAndTrucksForOrderDto with drivers and trucks for order
     */
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

    /**
     * Method for getting order status by id
     *
     * @param orderId id of order
     * @return OrderStatusDto with order status
     */
    @Transactional(readOnly = true)
    public OrderStatusDto getOrderStatusById(
            Long orderId
    ) {
        // Validate order and fetch it
        orderValidHelper.validateOrderAndFetch(orderId);

        // Return order status by order id
        return orderRepository.showOrderStatusByOrderId(orderId);
    }

    /**
     * Maps the given list of truck entities to a list of TruckInfoDto objects,
     * which contain the relevant information about each truck.
     *
     * @param trucksForOrderByWeight The list of truck entities to be mapped.
     * @return A list of TruckInfoDto objects containing the relevant information
     * about each truck in the input list.
     */
    private List<TruckInfoDto> mapTrucksToDto(
            List<TruckEntity> trucksForOrderByWeight
    ) {
        return trucksForOrderByWeight
                .stream()
                .map(truckMapper::toInfoDto)
                .toList();
    }

    /**
     * Retrieves the CountryMapEntity associated with the loading city specified
     * in the given CreateBaseOrder.
     *
     * @param createBaseOrder The base order containing route point information.
     * @return The CountryMapEntity for the loading city.
     * @throws EntityNotFoundException if no country map is found for the city.
     */
    private CountryMapEntity getCountryMapEntity(
            CreateBaseOrder createBaseOrder
    ) {
        // Stream through route points to find the first loading operation
        return createBaseOrder.routePointInfoDto()
                .stream()
                .filter(rp -> rp.operationType().equals(OperationType.LOADING.name()))
                .findFirst()
                .map(RoutePointInfoDto::cityName)
                .flatMap(countryMapRepository::findByCityName)
                .orElseThrow(() -> new EntityNotFoundException("Country map for city not found"));
    }


    /**
     * Find truck by id, create new TruckOrderEntity and return it in Set
     *
     * @param truckId     id of truck
     * @param orderEntity order entity
     * @return Set of TruckOrderEntity
     */
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

    /**
     * Creates a set of DriverOrderEntity instances from a list of DriverEntity and
     * an OrderEntity.
     *
     * @param allDriversById The list of DriverEntity instances.
     * @param orderEntity    The OrderEntity instance to be associated with the
     *                       DriverOrderEntity instances.
     * @return A set of DriverOrderEntity instances.
     */
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

    /**
     * Creates a new OrderEntity with a unique number, status set to NOT_COMPLETED
     * and associates it with the provided CountryMapEntity.
     *
     * @param countryMapEntity The CountryMapEntity to be associated with the order.
     * @return The newly created and saved OrderEntity.
     */
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

    /**
     * Maps a list of {@link RoutePointInfoDto} to a set of {@link RoutePointEntity} and saves them to the database.
     *
     * @param routePointInfoDto a list of route point information
     * @param orderEntity       the order to which the route points belong
     * @return a set of the saved route point entities
     */
    public Set<RoutePointEntity> saveRoutePoints(
            List<RoutePointInfoDto> routePointInfoDto,
            OrderEntity orderEntity
    ) {
        // Create a set to store the route point entities
        return routePointInfoDto
                .stream()
                .map(rp -> {
                    // Get the cargo entities for the route point
                    var cargoEntityList = rp.cargoInfo()
                            .stream()
                            .map(cargoMapper::toEntity)
                            .toList();

                    // Get the city entity for the route point
                    var cityEntity = cityRepository.findCityEntityByName(rp.cityName())
                            .orElseThrow(
                                    () -> new EntityNotFoundException("City with name = %s not found"
                                            .formatted(rp.cityName()))
                            );

                    // Create a new route point entity and set its order
                    var routePoint = routePointMapper.toEntity(rp, cargoEntityList, cityEntity);
                    routePoint.setOrder(orderEntity);

                    // Save the route point entity
                    return routePointRepository.save(routePoint);
                })
                .collect(Collectors.toSet());
    }

    /**
     * Maps a list of {@link DriverEntity} to a list of {@link DriverAllInfoDto}.
     * The mapping includes the driver's city and truck information.
     *
     * @param driversForOrder a list of driver entities
     * @return a list of driver all info dtos
     */
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

    /**
     * Calculates the total distance of all loading operations in the route points.
     *
     * @param routePointsDto list of route points for the order
     * @return total distance of loading operations
     */
    private static long getTotalDistance(
            List<RoutePointForOrderDto> routePointsDto
    ) {
        return routePointsDto.stream()
                .filter(rp -> OperationType.LOADING.toString().equals(rp.operationType()))
                .mapToLong(RoutePointForOrderDto::distance)
                .sum();
    }

    /**
     * Calculates the total weight of all cargoes in the route points.
     *
     * @param routePointsDto list of route points for the order
     * @return total weight of all cargoes
     */
    private static long getTotalWeight(
            List<RoutePointForOrderDto> routePointsDto
    ) {
        // Stream through the route points and then the cargoes of each route point
        return routePointsDto.stream()
                .flatMap(rp -> rp.cargos().stream())
                .mapToLong(CargoForOrderDto::weight)
                .sum();
    }

    /**
     * Finds a city entity by its name. If the city is not found, an
     * {@link EntityNotFoundException} is thrown.
     *
     * @param loadingCityName the name of the city to find
     * @return the city entity
     */
    private CityEntity findCityEntityByName(
            String loadingCityName
    ) {
        return cityRepository.findCityEntityByName(loadingCityName)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No city for loading operation. City name: " + loadingCityName));
    }

    /**
     * Finds the city name of the first loading operation in the given route points.
     *
     * @param routePointsDto list of route points for the order
     * @return the city name of the first loading operation
     * @throws IllegalArgumentException if no loading operation is found
     */
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

}
