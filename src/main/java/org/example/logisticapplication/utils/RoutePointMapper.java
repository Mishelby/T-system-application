package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointDto;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoutePointMapper {

    @Mappings({
            @Mapping(target = "id", source ="routePoint.id"),
            @Mapping(target = "city", source ="cityEntity"),
            @Mapping(target = "cargo", source ="cargoEntity")
    })
    RoutePointEntity toEntity(
            RoutePoint routePoint,
            CityEntity cityEntity,
            List<CargoEntity> cargoEntity
    );

    RoutePoint toDomain(RoutePointDto routePoint);

    RoutePointDto toDto(RoutePoint routePoint);

    RoutePoint toDomain(RoutePointEntity entity);


}
