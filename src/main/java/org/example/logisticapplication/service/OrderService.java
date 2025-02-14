package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Distance.DistanceInfo;
import org.example.logisticapplication.domain.Distance.DistanceInfoDto;
import org.example.logisticapplication.domain.Driver.OrderDefaultMessages;
import org.example.logisticapplication.domain.Order.OrderForSubmittingDto;
import org.example.logisticapplication.domain.Cargo.*;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.example.logisticapplication.domain.Driver.DriverAllInfoDto;
import org.example.logisticapplication.domain.Driver.DriverDefaultValues;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriversAndTrucksForOrderDto;
import org.example.logisticapplication.domain.Order.*;
import org.example.logisticapplication.domain.OrderCargo.OrderCargo;
import org.example.logisticapplication.domain.OrderDistanceEntity.OrderDistanceEntity;
import org.example.logisticapplication.domain.RoutePoint.*;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.Truck.TruckStatus;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.domain.User.UserEntity;
import org.example.logisticapplication.domain.UserOrders.UserOrderEntity;
import org.example.logisticapplication.mapper.*;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
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
    private final OrderCargoRepository orderCargoRepository;
    private final UserRepository userRepository;
    private final UserOrderRepository userOrderRepository;
    private final UserMapper userMapper;
    private final CityStationRepository cityStationRepository;
    private final StationDistanceRepository stationDistanceRepository;

    @Value("${default.size-of-submitting-orders}")
    private int defaultSize;
    private static final String LOADING_OPERATION = OperationType.LOADING.name();
    private static final String UNLOADING_OPERATION = OperationType.UNLOADING.name();
    private static final Double RECOMMENDED_DISTANCE_FOR_ONE_DRIVER = 800.0;

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
            String orderNumber
    ) {
        var orderEntity = orderRepository.findOrderEntityByNumber(orderNumber).orElseThrow();
        var loadingOperationCityId = getOperationCityId(orderEntity);
        long totalCargoWeight = totalWeight(orderEntity);

        var trucksForOrderByWeight = truckRepository.findTrucksForOrderByWeight(
                loadingOperationCityId,
                TruckStatus.SERVICEABLE.toString(),
                new BigDecimal(totalCargoWeight)
        );

        var distanceEntity = findDistanceEntityByOrderId(orderEntity);

        var trucksId = trucksForOrderByWeight
                .stream()
                .map(truckMapper::toDomain)
                .map(Truck::currentCityId)
                .collect(Collectors.toSet());


        var driversForOrder = driverRepository.findDriversByTruckId(
                trucksId,
                distanceEntity.getDistance(),
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
    public List<OrderInfo> gerOrdersForSubmit(
            DefaultSubmittingSize defaultSubmittingSize
    ) {

        if (defaultSubmittingSize == null) {
            defaultSubmittingSize = new DefaultSubmittingSize(0, defaultSize);
        }

        var pageRequest = PageRequest.of(
                defaultSubmittingSize.page(),
                defaultSubmittingSize.size() != null
                        ? defaultSubmittingSize.size()
                        : defaultSize,
                Sort.by(Sort.Direction.ASC, "id")
        );

        var ordersForSubmit = orderRepository.findOrdersForSubmit(pageRequest);

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
    public OrderInfoForUserDto sendOrderForUser(
            SendOrderForSubmittingDto orderDto
    ) {
        var infoDto = userMapper.toInfoDto(
                getUserEntity(orderDto.userId()),
                orderDto.departureDate().toString()
        );
        var cityFrom = orderDto.routePoint().getCityFrom();
        var cityTo = orderDto.routePoint().getCityTo();
        var cityNames = List.of(orderDto.routePoint().getCityFrom(), orderDto.routePoint().getCityTo());
        var stationByCityNames = cityStationRepository.findStationByCityNames(cityNames);

        var distancesByStationsIds = stationDistanceRepository.findDistancesByStationsIds(
                stationByCityNames
                        .stream()
                        .map(CityStationEntity::getId)
                        .toList()
        );

        var stationsByCityNames = stationByCityNames.stream()
                .collect(Collectors.toMap(
                        station -> station.getCity().getName(),
                        CityStationEntity::getName
                ));

        var distance = distancesByStationsIds.getDistance();
        var routePointInfo = new RoutePointInfoForUserDto(
                cityFrom,
                stationsByCityNames.get(cityFrom),
                cityTo,
                stationsByCityNames.get(cityTo),
                orderDto.routePoint().getCargoDto().getWeightKg(),
                distance
        );

        return new OrderInfoForUserDto(
                infoDto,
                routePointInfo,
                distanceMessageForUser(DistanceInfo::isRecommendDistance, distance, RECOMMENDED_DISTANCE_FOR_ONE_DRIVER)
        );
    }

    private DistanceInfoDto distanceMessageForUser(
            BiPredicate<Double, Double> biPredicate,
            Double distance1,
            Double distance2
    ) {
        return biPredicate.test(distance1, distance2)
                ? new DistanceInfoDto(OrderDefaultMessages.ONE_DRIVER_FOR_ORDER.getDescription())
                : new DistanceInfoDto(OrderDefaultMessages.MORE_DRIVER_FOR_ORDER.getDescription());
    }

    private UserEntity getUserEntity(
            Long id
    ) {
        return userRepository.findById(id).orElseThrow(
                () -> cityException(id)
        );
    }


    @Transactional(readOnly = true)
    public OrderForSubmittingDto responseOrderForSubmitting(
            SendOrderForSubmittingDto orderDto
    ) {
        var userEntity = userRepository.findById(orderDto.userId()).orElseThrow();
        var infoForRoutePoints = getInfoForRoutePointDto(
                orderDto.routePoint().getCityFrom(),
                orderDto.routePoint().getCityTo(),
                orderDto.routePoint().getCargoDto()
        );

        var cityFromEntity = cityRepository.findCityWithStations(infoForRoutePoints.cityFrom()).orElseThrow();
        var cityToEntity = cityRepository.findCityWithStations(infoForRoutePoints.cityTo()).orElseThrow();

        var stationNames = new HashSet<>(orderDto.routePoint().getStations());
        var stations = getStationEntityHashMap(cityFromEntity, cityToEntity, stationNames);
        var stationsId = getStationsId(cityFromEntity, cityToEntity, stationNames);

        var distance = cityStationRepository.findDistanceByIds(stationsId).orElseThrow().getDistance();
        var cargoInfoForDto = getCargoInfoForDto(infoForRoutePoints.cargo());

        var loadingRoutePoint = getRoutePointInfoDto(
                infoForRoutePoints.cityFrom(),
                stations.get(infoForRoutePoints.cityFrom()).getName(),
                cargoInfoForDto,
                OperationType.LOADING, distance
        );
        var unloadingRoutePoint = getRoutePointInfoDto(
                infoForRoutePoints.cityTo(),
                stations.get(infoForRoutePoints.cityFrom()).getName(),
                cargoInfoForDto,
                OperationType.UNLOADING,
                distance
        );

        return orderMapper.toDtoInfo(
                userMapper.toInfoDto(userEntity),
                List.of(loadingRoutePoint, unloadingRoutePoint)
        );
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
                null
        );

        return new CreateBaseOrder(routePointInfoDto);
    }


    @Transactional
    public void applyForOrder(
            String orderNumber,
            ApplyOrderDto applyOrderDto
    ) {
        var orderEntity = orderRepository.findOrderEntityByNumber(orderNumber).orElseThrow();
        var driversById = driverRepository.findDriversById(applyOrderDto.driversAndTrucks().driverIds());
        var trucksById = truckRepository.findTrucksById(applyOrderDto.driversAndTrucks().truckIds());
        var userEntity = userRepository.findById(applyOrderDto.userId()).orElseThrow();

        var driverOrderEntities = driversById.stream()
                .map(driver -> new DriverOrderEntity(orderEntity, driver))
                .collect(Collectors.toSet());

        var truckOrderEntities = trucksById.stream()
                .map(truck -> new TruckOrderEntity(orderEntity, truck))
                .collect(Collectors.toSet());

        var userOrderEntity = new UserOrderEntity(userEntity, orderEntity);

        orderEntity.getRoutePoints()
                .stream()
                .flatMap(rp -> rp.getCargo().stream())
                .forEach(cargo -> orderEntity.getOrderCargo().add(new OrderCargo(orderEntity, cargo)));

        orderCargoRepository.saveAll(orderEntity.getOrderCargo());

        addDriverToTruck(trucksById, driversById);
        clearOrderDriversAndTruckInfo(orderEntity);

        orderEntity.getDriverOrders().addAll(driverOrderEntities);
        orderEntity.getTruckOrders().addAll(truckOrderEntities);
        userOrderRepository.save(userOrderEntity);
    }

    private static List<Long> getStationsId(
            CityEntity cityFromEntity,
            CityEntity cityToEntity,
            HashSet<String> stationNames
    ) {
        return Stream.of(cityFromEntity, cityToEntity)
                .flatMap(city -> city.getCityStation().stream())
                .filter(station -> stationNames.contains(station.getName()))
                .map(CityStationEntity::getId)
                .toList();
    }

    private static HashMap<String, CityStationEntity> getStationEntityHashMap(
            CityEntity cityFromEntity,
            CityEntity cityToEntity,
            HashSet<String> stationNames
    ) {
        return Stream.of(cityFromEntity, cityToEntity)
                .flatMap(city -> city.getCityStation().stream())
                .filter(station -> stationNames.contains(station.getName()))
                .collect(Collectors.toMap(
                        cityStation -> cityStation.getCity().getName(),
                        Function.identity(),
                        (existing, value) -> existing,
                        HashMap::new));
    }

    public InfoForRoutePointDto getInfoForRoutePointDto(
            String cityFrom,
            String cityTo,
            BaseCargoDto baseCargoDto
    ) {
        return new InfoForRoutePointDto(
                cityFrom,
                cityTo,
                baseCargoDto
        );
    }

    private static CargoInfoForDto getCargoInfoForDto(
            BaseCargoDto cargoDto
    ) {
        return new CargoInfoForDto(
                new BigDecimal(cargoDto.getWeightKg()),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                CargoStatus.NOT_SHIPPED.name()
        );
    }

    private RoutePointInfoDto getRoutePointInfoDto(
            String city,
            String station,
            CargoInfoForDto cargoInfoForDto,
            OperationType operationType,
            Double distance
    ) {
        return new RoutePointInfoDto(
                city,
                station,
                List.of(cargoMapper.toInfoDto(cargoInfoForDto)),
                operationType.name(),
                distance
        );
    }

    private void addDriverToTruck(
            List<TruckEntity> trucksById,
            List<DriverEntity> driversById
    ) {
        int[] counter = {0};

        if (trucksById.size() == 1) {
            var firstTruck = trucksById.getFirst();
            addDriversToTruck(firstTruck, driversById);
            driversById.forEach(driver -> driver.setCurrentTruck(firstTruck));
        }

        if (trucksById.size() >= 2) {
            if (driversById.size() == 1) throw new IllegalArgumentException("Can't have more than 2 trucks");

            driversById.forEach(driver -> {
                var truckEntity = trucksById.get(counter[0]);
                truckEntity.getDrivers().add(driver);
                driver.setCurrentTruck(truckEntity);

                counter[0] = (counter[0] + 1) % trucksById.size();
            });

        }

    }

    private void addDriversToTruck(
            TruckEntity truck,
            List<DriverEntity> drivers
    ) {
        if ((truck.getDrivers().size() + drivers.size()) <= truck.getNumberOfSeats()) {
            truck.getDrivers().addAll(drivers);
        } else {
            throw new IllegalArgumentException("There is not enough space in the truck with number = %s!"
                    .formatted(truck.getRegistrationNumber()));
        }
    }

    private static void clearOrderDriversAndTruckInfo(
            OrderEntity orderEntity
    ) {
        orderEntity.getDriverOrders().clear();
        orderEntity.getTruckOrders().clear();
    }

    private DistanceEntity getDistanceEntity(
            OrderEntity orderEntity
    ) {
        return orderDistanceRepository.findDistanceEntityByOrder(orderEntity.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Distance entity for order with id = %s not found"
                                .formatted(orderEntity.getId())
                        ));
    }

    private DistanceEntity getDistanceEntity(
            Set<RoutePointEntity> routePointEntities
    ) {
        var cityEntityFrom = routePointEntities.stream()
                .filter(rp -> LOADING_OPERATION.equals(rp.getOperationType()))
                .map(RoutePointEntity::getCity)
                .toList().getFirst();

        var cityEntityTo = routePointEntities.stream()
                .filter(rp -> UNLOADING_OPERATION.equals(rp.getOperationType()))
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

    private DistanceEntity findDistanceEntityByOrderId(OrderEntity orderEntity) {
        return orderDistanceRepository.findDistanceEntityByOrder(orderEntity.getId()).orElseThrow(
                () -> new EntityNotFoundException("Distance entity for order with id = %s not found"
                        .formatted(orderEntity.getId()))
        );
    }

    private static long totalWeight(OrderEntity orderEntity) {
        return orderEntity.getRoutePoints().stream()
                .filter(rp -> rp.getOperationType().equals(LOADING_OPERATION))
                .flatMap(rp -> rp.getCargo().stream())
                .map(CargoEntity::getWeightKg)
                .mapToLong(Long::longValue)
                .sum();
    }

    private static Long getOperationCityId(OrderEntity orderEntity) {
        return orderEntity.getRoutePoints().stream()
                .filter(rp -> rp.getOperationType().equals(LOADING_OPERATION))
                .map(RoutePointEntity::getCity)
                .findFirst()
                .get()
                .getId();
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

    // TODO Переделать метод, что бы он принимал сразу список маршрутных точек
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
                null
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
                        null,
                        List.of(cargoInfoDto),
                        LOADING_OPERATION,
                        null
                )
        );

        routePointInfoDto.add(
                new RoutePointInfoDto(
                        cityTo,
                        null,
                        List.of(cargoInfoDto),
                        UNLOADING_OPERATION,
                        null
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
                .filter(rp -> rp.getOperationType().equals(LOADING_OPERATION))
                .findFirst()
                .map(RoutePointInfoDto::getCityName)
                .flatMap(countryMapRepository::findByCityName)
                .orElseThrow(() -> new EntityNotFoundException("Country map for city not found"));
    }

    private OrderEntity createNewOrder(
            CountryMapEntity countryMapEntity
    ) {
        var orderEntity = new OrderEntity(
                UUID.randomUUID().toString(),
                countryMapEntity
        );

        orderMapper.defaultValueForOrderCargo(orderEntity);
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
                            .filter(cargo -> LOADING_OPERATION.equals(rp.getOperationType()))
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
                }).collect(Collectors.toSet());
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

    public static Double calculationTimeToOrder(
            Double distance,
            Long averageSpeed
    ) {
        return distance / averageSpeed;
    }

    private <T> RuntimeException cityException(T value) {
        return new IllegalArgumentException("City with value = %s not found!".formatted(value));
    }

}
