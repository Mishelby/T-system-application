package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.City.CityInfoDto;
import org.example.logisticapplication.domain.Driver.*;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;

import org.example.logisticapplication.domain.DriverStatus.DriverStatusEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Role.Role;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {

    @Mappings({
            @Mapping(target = "numberOfHoursWorked", defaultValue = "0")
    })
    DriverEntity toEntity(Driver driver);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "driver.name"),
            @Mapping(target = "password", source = "encoderPassword"),
            @Mapping(target = "numberOfHoursWorked", expression = "java(getDefaultNumberOfHoursWorked())"),
            @Mapping(target = "currentCity", source = "cityEntity"),
            @Mapping(target = "roles", source = "setOfRoles"),
    })
    DriverEntity toEntity(
            DriverRegistrationDto driver,
            String encoderPassword,
            CityEntity cityEntity,
            Set<Role> setOfRoles
    );

    @AfterMapping
    default void ensureStatus(@MappingTarget DriverEntity driver) {
        if(driver.getDriverStatus() == null) {
            driver.setDriverStatus(new DriverStatusEntity(ShiftStatus.REST.getStatusName()));
        }
    }

    @AfterMapping
    default void ensureRoles(@MappingTarget DriverEntity driver) {
        if(driver.getRoles() == null) {
            driver.setRoles(new HashSet<>());
        }
    }

    @Named("defaultDriverStatus")
    default DriverStatusEntity getDefaultDriverStatus() {
        return new DriverStatusEntity(ShiftStatus.REST.getStatusName());
    }

    @Named("defaultNumberOfHoursWorked")
    default Double getDefaultNumberOfHoursWorked() {
        return 0.0;
    }

    @Mappings({
            @Mapping(target = "currentCityId", source = "currentCity.id"),
    })
    Driver toDomain(DriverEntity entity);

    DriverDto toDto(Driver driver);

    Driver toDomain(DriverDto driverDto);

    @Mappings({
            @Mapping(target = "id", source = "entity.id"),
            @Mapping(target = "name", source = "entity.name"),
            @Mapping(target = "secondName", source = "entity.secondName"),
            @Mapping(target = "personNumber", source = "entity.personNumber"),
            @Mapping(target = "numberOfHoursWorked", source = "entity.numberOfHoursWorked"),
            @Mapping(target = "status", expression = "java(statusToString(entity.getDriverStatus()))"),
            @Mapping(target = "currentCityInfo", source = "currentCityInfo"),
            @Mapping(target = "currentTruckInfo", source = "currentTruckInfo")
    })
    DriverAllInfoDto toDtoInfo(
            DriverEntity entity,
            CityInfoDto currentCityInfo,
            TruckInfoDto currentTruckInfo
    );

    default String statusToString(DriverStatusEntity status) {
        return status != null ? status.getStatus() : ShiftStatus.REST.getStatusName();
    }

    @Mappings({
            @Mapping(target = "name", source = "driverEntity.name"),
            @Mapping(target = "number", source = "driverEntity.personNumber"),
            @Mapping(target = "currentCity", source = "driverEntity.currentCity.name"),
            @Mapping(target = "truckNumber", source = "driverEntity.currentTruck.registrationNumber"),
            @Mapping(target = "status", expression = "java(currentDriverStatus(driverEntity,orderEntity))")
    })
    MainDriverInfoDto toMainInfo(
            DriverEntity driverEntity,
            List<OrderEntity> orderEntity
    );

    @Mappings({
            @Mapping(target = "name", source = "driverEntity.name"),
            @Mapping(target = "number", source = "driverEntity.personNumber"),
            @Mapping(target = "currentCity", source = "driverEntity.currentCity.name"),
            @Mapping(target = "truckNumber", source = "driverEntity.currentTruck.registrationNumber"),
            @Mapping(target = "status", expression = "java(currentDriverStatus(driverEntity,orderEntity))")
    })
    MainDriverInfoDto toMainInfo(
            DriverEntity driverEntity,
            OrderEntity orderEntity
    );

    @Named(value = "currentStatusForDriver")
    default String currentDriverStatus(
            DriverEntity driverEntity,
            OrderEntity orderEntity
    ) {
        boolean result = orderEntity.getDriverOrders()
                .stream()
                .map(DriverOrderEntity::getDriver)
                .anyMatch(driver -> driver.getId().equals(driverEntity.getId()));

        return result ? "Driver on order" : "The driver is available";
    }

    @Named(value = "currentStatusForDriver")
    default String currentDriverStatus(
            DriverEntity driverEntity,
            List<OrderEntity> orderEntity
    ) {
        boolean result = orderEntity.stream()
                .flatMap(order -> order.getDriverOrders().stream())
                .map(DriverOrderEntity::getDriver)
                .anyMatch(driver -> driver.getId().equals(driverEntity.getId()));

        return result ? "Driver on order" : "The driver is available";
    }

    @Mappings({
            @Mapping(target = "currentLocation", source = "entity.currentCity.name"),
            @Mapping(target = "currentTruckNumber", source = "entity.currentTruck.registrationNumber")
    })
    DriverOrderInfo toOrderInfo(DriverEntity entity);

    @Mappings({
            @Mapping(target = "currentLocation", source = "entity.currentCity.name"),
            @Mapping(target = "currentTruckNumber", source = "entity.currentTruck.registrationNumber")
    })
    List<DriverOrderInfo> toOrderInfo(List<DriverEntity> entity);

    @Mappings({
            @Mapping(target = "driverId", source = "entity.id"),
            @Mapping(target = "name", source = "entity.name"),
            @Mapping(target = "secondName", source = "entity.secondName"),
            @Mapping(target = "currentCityName", source = "entity.currentCity.name"),
    })
    DriverWithoutTruckDto toDtoWithoutTruck(DriverEntity entity);

}
