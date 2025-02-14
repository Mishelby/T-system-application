package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.*;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Order.OrderMainInfo;
import org.example.logisticapplication.domain.RoutePoint.MainRoutePointInfoDto;
import org.example.logisticapplication.domain.RoutePoint.OperationType;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.mapper.*;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.DriverValidHelper;
import org.example.logisticapplication.utils.RoleName;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final DriverMapper driverMapper;
    private final CityRepository cityRepository;
    private final OrderRepository orderRepository;
    private final RoutePointMapper routePointMapper;
    private final CargoMapper cargoMapper;
    private final TruckMapper truckMapper;

    public static final String NO_CURRENT_ORDERS_MESSAGE = "No current orders";
    private final OrderDistanceRepository orderDistanceRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DriverBaseInfoDto createDriver(
            DriverRegistrationInfo driverInfo
    ) {
        isDriverExistsByPersonNumber(driverInfo);
        var roles = roleService.findAll();

        var cityEntity = cityRepository.findCityEntityByName(driverInfo.getCityName())
                .orElseThrow();

        var driverEntity = driverMapper.toEntity(
                getEncodePassword(driverInfo),
                driverInfo,
                cityEntity,
                Set.of(roles.get(RoleName.DRIVER))
        );

        return driverMapper.toBaseDto(
                driverRepository.save(driverEntity)
        );
    }

    private void isDriverExistsByPersonNumber(
            DriverRegistrationInfo driver
    ) {
        if (driverRepository.existsByPersonNumber(driver.getPersonNumber())) {
            throw new IllegalArgumentException(
                    "Driver already exists with person number=%s"
                            .formatted(driver.getPersonNumber())
            );
        }
    }

    @Transactional(readOnly = true)
    public List<Driver> findAll(
            String status,
            String cityName
    ) {
        var allDrivers = driverRepository.findAllDrivers(status, cityName);

        if (allDrivers.isEmpty()) {
            return new ArrayList<>();
        }

        return allDrivers
                .stream()
                .map(driverMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Driver findById(
            Long id
    ) {
        return driverMapper.toDomain(
                driverRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException(
                                DriverValidHelper.getDEFAULT_MESSAGE().formatted(id)
                        )
                ));
    }

    @Transactional
    public void deleteDriver(
            Long id
    ) {
        var driverEntity = driverRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        DriverValidHelper.getDEFAULT_MESSAGE().formatted(id)
                )
        );

        var allByCurrentDriver = truckRepository.findAllByCurrentDriver(driverEntity);

        for (TruckEntity truckEntity : allByCurrentDriver) {
            truckEntity.getDrivers().remove(driverEntity);
        }

        driverEntity.setCurrentTruck(null);
        driverRepository.delete(driverEntity);
    }

    @Transactional
    public Driver updateDriver(
            Long id,
            Driver updateDriver
    ) {
        if (!driverRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    DriverValidHelper.getDEFAULT_MESSAGE().formatted(id)
            );
        }

        driverRepository.updateCurrentDriver(
                id,
                updateDriver.name(),
                updateDriver.secondName(),
                updateDriver.personNumber(),
                updateDriver.numberOfHoursWorked(),
                null,
                updateDriver.currentCityId(),
                updateDriver.currentTruckId()
        );

        return driverMapper.toDomain(
                driverRepository.findById(id).orElseThrow()
        );
    }

    @Transactional(readOnly = true)
    public DriverInfo getDriverInfo(
            Long driverId
    ) {
        var driverEntity = driverRepository.findById(driverId).orElseThrow();
        var orderEntity = orderRepository.findOrderEntitiesByDriverId(driverEntity.getId()).orElse(null);

        if (orderEntity == null) {
            return new DriverMainInfoWithoutOrder(
                    driverEntity.getName(),
                    driverEntity.getPersonNumber().toString(),
                    NO_CURRENT_ORDERS_MESSAGE
            );
        }

        var driverNumbers = getDriverNumbers(driverId, orderEntity);

        var routePointInfoDto = getMainOrderInfo(orderEntity);
        var truckInfoDto = getTruckInfoDto(orderEntity);
        var orderMainInfo = getOrderMainInfo(orderEntity, routePointInfoDto, driverNumbers, truckInfoDto);

        return new DriverMainInfoDto(
                driverId,
                driverEntity.getName(),
                driverEntity.getPersonNumber().toString(),
                orderMainInfo
        );
    }

    @Transactional(readOnly = true)
    public DriverInfoDto getInfoForDriver(
            Long driverId
    ) {
        var driverEntity = driverRepository.findById(driverId).orElseThrow();
        var orderEntity = getOrderEntity(driverId);

        var allDriverInOrder = orderEntity.getDriverOrders()
                .stream()
                .filter(driverOrderEntity -> !driverOrderEntity.getDriver().getId().equals(driverId))
                .flatMap(dor -> dor.getDriver().getCurrentTruck().getDrivers().stream())
                .toList();

        var firstDriver = allDriverInOrder.getFirst();

        return new DriverInfoDto(
                driverEntity.getPersonNumber().toString(),
                getNumbersAnotherDrivers(allDriverInOrder),
                firstDriver.getCurrentTruck().getRegistrationNumber(),
                orderEntity.getUniqueNumber(),
                getRoutePointInfoDto(orderEntity, driverEntity)
        );
    }

    @Transactional(readOnly = true)
    public Long findDriverIdByPersonNumber(Long personNumber) {
        return driverRepository.findDriverEntityByPersonNumber(personNumber)
                .orElseThrow(() -> new EntityNotFoundException("No driver entity found for person number = %s"))
                .getId();
    }

    @Transactional(readOnly = true)
    public HttpStatus driverLogin(
            DriverLoginInfo driverLoginInfo
    ) {
        return driverRepository.findDriverEntityByPersonNumber(driverLoginInfo.personNumber())
                .filter(driver -> passwordEncoder.matches(driverLoginInfo.password(), driver.getPassword()))
                .map(driver -> HttpStatus.OK)
                .orElse(HttpStatus.UNAUTHORIZED);
    }

    private static OrderMainInfo getOrderMainInfo(
            OrderEntity orderEntity,
            List<MainRoutePointInfoDto> routePoint,
            List<String> anotherDriverNumbers,
            List<TruckInfoDto> truckInfoDto
    ) {
        return new OrderMainInfo(
                orderEntity.getUniqueNumber(),
                orderEntity.getStatus().getStatusName(),
                orderEntity.getCountryMap().getCountryName(),
                anotherDriverNumbers,
                routePoint,
                truckInfoDto
        );
    }

    private List<MainRoutePointInfoDto> getMainOrderInfo(
            OrderEntity orderEntity
    ) {
        return orderEntity.getRoutePoints()
                .stream()
                .filter(rp -> rp.getOperationType().equals(OperationType.LOADING.name()))
                .map(rp -> {
                    var cityFrom = getCity(orderEntity, OperationType.LOADING.name());
                    var cityTo = getCity(orderEntity, OperationType.UNLOADING.name());
                    var distance = getDistanceEntity(orderEntity);

                    var cargo = rp.getCargo().stream()
                            .map(cargoMapper::toMainInfo)
                            .toList();

                    return routePointMapper.toMainInfo(
                            cityFrom,
                            cityTo,
                            distance,
                            !cargo.isEmpty()
                                    ? cargo.getFirst()
                                    : null
                    );
                }).toList();
    }

    private Long getDistanceEntity(
            OrderEntity orderEntity
    ) {
        return orderDistanceRepository.findDistanceEntityByOrder(orderEntity.getId()).orElseThrow(
                () -> new EntityNotFoundException("No distance entity found for order with number = %s"
                        .formatted(orderEntity.getUniqueNumber()))
        ).getDistance();
    }

    private static String getCity(
            OrderEntity orderEntity,
            String operationType
    ) {
        return orderEntity.getRoutePoints()
                .stream()
                .filter(rp -> rp.getOperationType().equals(operationType))
                .map(RoutePointEntity::getCity)
                .map(CityEntity::getName)
                .findFirst()
                .get();
    }

    private static List<String> getDriverNumbers(
            Long driverId,
            OrderEntity orderEntity
    ) {
        return orderEntity.getDriverOrders().stream()
                .map(DriverOrderEntity::getDriver)
                .filter(driver -> !driver.getId().equals(driverId))
                .map(DriverEntity::getPersonNumber)
                .map(String::valueOf)
                .toList();
    }

    private List<TruckInfoDto> getTruckInfoDto(
            OrderEntity orderEntity
    ) {
        return orderEntity.getTruckOrders()
                .stream()
                .map(TruckOrderEntity::getTruck)
                .map(truckMapper::toInfoDto)
                .toList();
    }

    private OrderEntity getOrderEntity(Long orderId) {
        return orderRepository.findOrderEntitiesByDriverId(orderId).orElseThrow(
                () -> new IllegalArgumentException(
                        "Order does not exist with id = %s"
                                .formatted(orderId)
                )
        );
    }

    private List<RoutePointInfoDto> getRoutePointInfoDto(
            OrderEntity orderEntity,
            DriverEntity driverEntity
    ) {
        var distanceEntity = orderEntity.getCountryMap().getDistances()
                .stream()
                .filter(distances -> distances.getFromCity().getId().equals(driverEntity.getCurrentCity().getId()))
                .findFirst().orElseThrow(
                        () -> new IllegalArgumentException("No distances for city with %s and %s")
                );

        var routePointInfoDto = orderEntity.getRoutePoints().stream()
                .map(rp -> {
                    var cargoInfoDtoSet = rp.getCargo().stream()
                            .map(cargoMapper::toDtoInfo)
                            .collect(Collectors.toSet());

                    return routePointMapper.toInfoDto(rp, cargoInfoDtoSet);
                })
                .toList();

        routePointInfoDto.forEach(rp -> rp.setDistance((double) distanceEntity.getDistance()));

        return routePointInfoDto;
    }

    private static Set<String> getNumbersAnotherDrivers(
            List<DriverEntity> drivers
    ) {
        return drivers.stream()
                .map(DriverEntity::getPersonNumber)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public List<DriverWithoutTruckDto> findAllDiversWithoutTruck() {
        var driversWithoutTruck = driverRepository.findDriversWithoutTruck();

        if (driversWithoutTruck.isEmpty()) {
            return Collections.emptyList();
        }

        return driversWithoutTruck.stream()
                .map(driverMapper::toDtoWithoutTruck)
                .toList();
    }

    private String getEncodePassword(DriverRegistrationInfo driverInfo) {
        return passwordEncoder.encode(driverInfo.getPassword());
    }
}
