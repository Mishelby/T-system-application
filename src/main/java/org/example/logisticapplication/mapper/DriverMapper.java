package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.City.CityInfoDto;
import org.example.logisticapplication.domain.Driver.*;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;

import org.example.logisticapplication.domain.DriverStatus.DriverStatusEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Role.RoleEntity;
import org.example.logisticapplication.domain.ShiftStatus.ShiftStatusEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.User.UserEntity;
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
            @Mapping(target = "name", expression = "java(driverInfo.getName())"),
            @Mapping(target = "username", expression = "java(driverInfo.getUserName())"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "secondName", expression = "java(driverInfo.getSecondName())"),
            @Mapping(target = "personNumber", expression = "java(driverInfo.getPersonNumber())"),
            @Mapping(target = "currentCity", source = "currentCity"),
            @Mapping(target = "roleEntities", source = "rolesSet"),
    })
    DriverEntity toEntity(
            String password,
            DriverRegistrationInfo driverInfo,
            CityEntity currentCity,
            Set<RoleEntity> rolesSet
    );

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", expression = "java(driverInfo.getName())"),
            @Mapping(target = "username", expression = "java(driverInfo.getUserName())"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "secondName", expression = "java(driverInfo.getSecondName())"),
            @Mapping(target = "personNumber", expression = "java(driverInfo.getPersonNumber())"),
            @Mapping(target = "currentCity", source = "currentCity"),
            @Mapping(target = "roleEntities", source = "rolesSet"),
            @Mapping(target = "driverStatus", source = "driverStatusEntity"),
            @Mapping(target = "shiftStatus", source = "shiftStatusEntity"),
    })
    DriverEntity toEntity(
            String password,
            DriverRegistrationInfo driverInfo,
            CityEntity currentCity,
            Set<RoleEntity> rolesSet,
            DriverStatusEntity driverStatusEntity,
            ShiftStatusEntity shiftStatusEntity
    );

    @Mappings({
            @Mapping(target = "name", source = "driver.name"),
            @Mapping(target = "secondName", source = "driver.secondName"),
            @Mapping(target = "personNumber", source = "driver.personNumber"),
            @Mapping(target = "currentCityName", source = "driver.currentCity.name")
    })
    DriverBaseInfoDto toBaseDto(DriverEntity driver);


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "driver.name"),
            @Mapping(target = "password", source = "encoderPassword"),
            @Mapping(target = "numberOfHoursWorked", expression = "java(getDefaultNumberOfHoursWorked())"),
            @Mapping(target = "currentCity", source = "cityEntity"),
    })
    DriverEntity toEntity(
            DriverRegistrationDto driver,
            String encoderPassword,
            CityEntity cityEntity
    );

    @AfterMapping
    default void ensureHoursOfWorked(@MappingTarget DriverEntity driverEntity) {
        if (driverEntity.getNumberOfHoursWorked() == null) {
            driverEntity.setNumberOfHoursWorked(0.0);
        }
    }

    @AfterMapping
    default void ensureDriverStatus(@MappingTarget DriverEntity driver) {
        if (driver.getDriverStatus() == null) {
            driver.setDriverStatus(new DriverStatusEntity(DriverStatus.NOT_DRIVING));
        }
    }

    @AfterMapping
    default void ensureShiftStatus(@MappingTarget DriverEntity driver) {
        if (driver.getShiftStatus() == null) {
            driver.setShiftStatus(new ShiftStatusEntity(ShiftStatus.REST));
        }
    }

    @AfterMapping
    default void ensureRoles(@MappingTarget DriverEntity driver) {
        if (driver.getRoleEntities() == null) {
            driver.setRoleEntities(new HashSet<>());
        }
    }

    @Mappings({
            @Mapping(target = "id", source = "driverEntity.id"),
            @Mapping(target = "name", source = "driverEntity.name"),
            @Mapping(target = "currenCityName", source = "driverEntity.currentCity.name"),
            @Mapping(target = "numOfHoursWorked", source = "driverEntity.numberOfHoursWorked"),
    })
    DriverInfoForOrderDto toDriverInfoDto(DriverEntity driverEntity);

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
        if (status.getStatus().getStatusName() != null) {
            return status.getStatus().getStatusName();
        }

        return null;
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
