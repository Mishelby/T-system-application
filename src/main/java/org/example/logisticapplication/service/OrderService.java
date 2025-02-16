package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Distance.DistanceInfo;
import org.example.logisticapplication.domain.Distance.DistanceInfoDto;
import org.example.logisticapplication.domain.Driver.OrderDefaultMessages;
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
import org.example.logisticapplication.domain.OrderCargo.OrderCargoEntity;
import org.example.logisticapplication.domain.OrderStatusEntity.OrderStatusEntity;
import org.example.logisticapplication.domain.RoutePoint.*;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.Truck.TruckStatus;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.domain.User.UserEntity;
import org.example.logisticapplication.domain.User.UserInfoDto;
import org.example.logisticapplication.domain.UserOrderInfoEntity;
import org.example.logisticapplication.domain.UserOrders.UserOrderEntity;
import org.example.logisticapplication.mapper.*;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
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
    private final DriverDefaultValues defaultValues;
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CargoMapper cargoMapper;
    private final OrderDistanceRepository orderDistanceRepository;
    private final OrderCargoRepository orderCargoRepository;
    private final UserRepository userRepository;
    private final UserOrderRepository userOrderRepository;
    private final UserMapper userMapper;
    private final CityStationRepository cityStationRepository;
    private final StationDistanceRepository stationDistanceRepository;
    private final RoutePointRepository routePointRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final UserOrderInfoRepository userOrderInfoRepository;
    private final RoutePointMapper routePointMapper;

    @Value("${driver.recommendDistance}")
    private Double oneDriverRecommendedDistance;
    private final LocalDateTimeFormatter dateTimeFormatter;

    private static final String LOADING_OPERATION = OperationType.LOADING.name();

    @Transactional
    public BaseOrderInfo createBaseOrder(
            OrderInfoForUserDto orderInfo
    ) {
        isValidDateForOrder(dateTimeFormatter.convertToLocalDate(getDesiredDate(orderInfo)));

        var countryMapEntity = getCountryMapEntity(orderInfo);
        var orderEntity = createNewOrder(countryMapEntity);
        var routePointEntities = saveRoutePoints(orderInfo.routePointInfo(), orderEntity);
        var user = getUserEntity(orderInfo.userInfoDto().name());
        setUserOrderEntity(user, orderEntity);
        setRoutePointsForOrder(orderEntity, routePointEntities);
        var updatedOrder = orderRepository.save(orderEntity);
        userOrderInfoRepository.save(
                new UserOrderInfoEntity(
                        updatedOrder.getUniqueNumber(),
                        user.getUsername(),
                        orderInfo.routePointInfo().cityFrom(),
                        orderInfo.routePointInfo().stationFrom(),
                        orderInfo.routePointInfo().cityTo(),
                        orderInfo.routePointInfo().stationTo(),
                        orderInfo.routePointInfo().weight(),
                        orderInfo.routePointInfo().distance(),
                        orderInfo.distanceInfoDto().message()
                ));

        return new BaseOrderInfo(
                updatedOrder.getUniqueNumber(),
                orderInfo.userInfoDto()
        );
    }

    public void isValidDateForOrder(LocalDate date) {
        var today = LocalDate.now();

        if (date.isBefore(today)) {
            throw new IllegalArgumentException("Date must be before today!");
        }
    }

    private UserEntity getUserEntity(
            String name
    ) {
        return userRepository.findByName(name).orElseThrow(
                () -> exception("User", name)
        );
    }

    private void setUserOrderEntity(
            UserEntity user,
            OrderEntity orderEntity
    ) {
        var userOrderEntity = new UserOrderEntity(user, orderEntity);
        userOrderRepository.save(userOrderEntity);
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

        var stationsByCityNames = stationByCityNames
                .stream()
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
                distanceMessageForUser(
                        DistanceInfo::isRecommendDistance,
                        distance,
                        oneDriverRecommendedDistance
                )
        );
    }

    @Transactional(readOnly = true)
    public List<BaseOrderForSubmitDto> findOrdersForSubmit(
            DefaultSubmittingSize defaultSubmittingSize
    ) {

        if (defaultSubmittingSize == null) {
            defaultSubmittingSize = new DefaultSubmittingSize(0, 5);
        }

        var pageable = PageRequest.of(
                defaultSubmittingSize.page() != null
                        ? defaultSubmittingSize.page()
                        : 0,
                defaultSubmittingSize.size() != null
                        ? defaultSubmittingSize.size()
                        : 5
        );

        var ordersForSubmit = orderRepository.findOrdersForSubmit(pageable);
        var userOrderEntityMap = getUserOrderEntityMap(ordersForSubmit);
        var userEntityMap = getUserEntityMap(userOrderEntityMap);

        return ordersForSubmit.stream()
                .map(order -> {
                    var orderBaseInfoDto = orderRepository.findOrderDtoById(order.getId());
                    var userOrderEntity = userOrderEntityMap.get(order.getUniqueNumber());
                    var user = userEntityMap.get(userOrderEntity);
                    var userOrderInfo = getUserOrderInfo(order, user);

                    var userInfoDto = new UserInfoDto(
                            user.getUsername(),
                            order.getCreateAt().toString(),
                            null
                    );

                    return routePointMapper.toInfoDto(
                            orderBaseInfoDto,
                            userInfoDto,
                            routePointMapper.toInfoDto(userOrderInfo),
                            new DistanceInfoDto(userOrderInfo.getDistanceInfo())
                    );
                }).toList();
    }

    @Transactional(readOnly = true)
    public OrderStatusDto getOrderStatusById(
            Long orderId
    ) {
        orderValidHelper.validateOrderAndFetch(orderId);
        return orderRepository.showOrderStatusByOrderId(orderId);
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
                .forEach(cargo -> orderEntity.getOrderCargoEntities().add(new OrderCargoEntity(orderEntity, cargo)));

        orderCargoRepository.saveAll(orderEntity.getOrderCargoEntities());

        addDriverToTruck(trucksById, driversById);
        clearOrderDriversAndTruckInfo(orderEntity);

        orderEntity.getDriverOrders().addAll(driverOrderEntities);
        orderEntity.getTruckOrders().addAll(truckOrderEntities);
        userOrderRepository.save(userOrderEntity);
    }

    private UserOrderInfoEntity getUserOrderInfo(
            OrderEntity order,
            UserEntity user
    ) {
        return userOrderInfoRepository.findUserOrderInfo(
                user.getUsername(),
                order.getUniqueNumber()
        ).orElseThrow(
                () -> new IllegalStateException("User order info for username = %s and order number = %s not found"
                        .formatted(user.getUsername(), order.getUniqueNumber())
                )
        );
    }

    private DistanceInfoDto distanceMessageForUser(
            BiPredicate<Double, Double> biPredicate,
            Double distance1,
            Double distance2
    ) {
        return biPredicate.test(distance1, distance2)
                ? new DistanceInfoDto(OrderDefaultMessages.MORE_DRIVER_FOR_ORDER.getDescription())
                : new DistanceInfoDto(OrderDefaultMessages.ONE_DRIVER_FOR_ORDER.getDescription());
    }

    private UserEntity getUserEntity(
            Long id
    ) {
        return userRepository.findById(id).orElseThrow(
                () -> exception("User", id)
        );
    }

    private static Map<UserOrderEntity, UserEntity> getUserEntityMap(
            Map<String, UserOrderEntity> userOrderEntityMap
    ) {
        return userOrderEntityMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getValue,
                        entry -> entry.getValue().getUser()
                ));

    }

    private Map<String, UserOrderEntity> getUserOrderEntityMap(
            List<OrderEntity> ordersForSubmit
    ) {
        return ordersForSubmit
                .stream()
                .collect(Collectors.toMap(
                        OrderEntity::getUniqueNumber,
                        order -> userOrderRepository.findByOrderId(order.getId())
                                .orElseThrow(() -> exception("User order", order.getId()))
                ));
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

    public void setRoutePointsForOrder(
            OrderEntity orderEntity,
            Set<RoutePointEntity> routePointEntities
    ) {
        orderEntity.setRoutePoints(routePointEntities);

        routePointEntities
                .stream()
                .flatMap(rp -> rp.getCargo().stream())
                .forEach(orderCargo ->
                        orderEntity.getOrderCargoEntities().add(
                                orderCargoRepository.save(
                                        new OrderCargoEntity(orderEntity, orderCargo)
                                )
                        )
                );

        orderEntity.setDriverOrders(null);
        orderEntity.setTruckOrders(null);
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

    private List<TruckInfoDto> mapTrucksToDto(
            List<TruckEntity> trucksForOrderByWeight
    ) {
        return trucksForOrderByWeight
                .stream()
                .map(truckMapper::toInfoDto)
                .toList();
    }

    private CountryMapEntity getCountryMapEntity(
            OrderInfoForUserDto orderInfo
    ) {
        return countryMapRepository.findByCityName(orderInfo.routePointInfo().cityFrom())
                .orElseThrow(() -> exception("Country map", orderInfo.routePointInfo().cityFrom()));
    }

    private OrderEntity createNewOrder(
            CountryMapEntity countryMapEntity
    ) {
        var ordersStatus = orderStatusRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                                OrderStatusEntity::getStatusName,
                                Function.identity()
                        )
                );

        var orderEntity = new OrderEntity(
                UUID.randomUUID().toString(),
                ordersStatus.get(OrderStatus.NOT_COMPLETED.getName()),
                countryMapEntity
        );

        orderMapper.defaultValueForOrderCargo(orderEntity);
        return orderRepository.save(orderEntity);
    }

    public Set<RoutePointEntity> saveRoutePoints(
            RoutePointInfoForUserDto routePointInfoDto,
            OrderEntity orderEntity
    ) {

        var cargoInfoDto = toCargoInfoDto(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                routePointInfoDto.weight(),
                CargoStatus.NOT_SHIPPED
        );
        var cargo = cargoMapper.toEntity(cargoInfoDto);

        var routePoints = Stream.of(
                new RoutePointEntity(
                        getCityEntity(routePointInfoDto.cityFrom()),
                        OperationType.LOADING.name(),
                        new ArrayList<>(Collections.singletonList(cargo)),
                        orderEntity
                ),
                new RoutePointEntity(
                        getCityEntity(routePointInfoDto.cityTo()),
                        OperationType.UNLOADING.name(),
                        new ArrayList<>(Collections.singletonList(cargo)),
                        orderEntity
                )
        ).toList();
        routePointRepository.saveAll(routePoints);

        return new HashSet<>(routePoints);
    }

    private CargoInfoDto toCargoInfoDto(
            String name,
            String number,
            Double weight,
            CargoStatus status
    ) {
        return new CargoInfoDto(
                name,
                number,
                weight,
                status.name()
        );
    }

    private CityEntity getCityEntity(
            String cityName
    ) {
        return cityRepository.findCityEntityByName(cityName).orElseThrow(
                () -> exception("City", cityName)
        );
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

    private <T> RuntimeException exception(T object, T value) {
        return new IllegalArgumentException("%s with value = %s not found!".formatted(object, value));
    }

    private static String getDesiredDate(
            OrderInfoForUserDto orderInfo
    ) {
        return orderInfo.userInfoDto().desiredDate();
    }
}
