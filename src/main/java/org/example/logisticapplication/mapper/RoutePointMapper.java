package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.Cargo.CargoInfoDto;
import org.example.logisticapplication.domain.Cargo.MainCargoInfoDto;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.RoutePoint.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoutePointMapper {

    @Mappings({
            @Mapping(target = "id", source = "routePoint.id"),
            @Mapping(target = "city", source = "cityEntity"),
            @Mapping(target = "cargo", source = "cargoEntity")
    })
    RoutePointEntity toEntity(
            RoutePoint routePoint,
            CityEntity cityEntity,
            List<CargoEntity> cargoEntity
    );

    @Mappings({
            @Mapping(target = "operationType", source = "routePointInfoDto.operationType"),
            @Mapping(target = "city", source = "cityEntity"),
            @Mapping(target = "cargo", source = "cargoEntities"),
            @Mapping(target = "id", ignore = true)
    })
    RoutePointEntity toEntity(
            RoutePointInfoDto routePointInfoDto,
            List<CargoEntity> cargoEntities,
            CityEntity cityEntity
    );

    @Mappings({
            @Mapping(target = "cityFrom", source = "valueFrom"),
            @Mapping(target = "cityTo", source = "valueTo"),
            @Mapping(target = "distance", source = "distance"),
            @Mapping(target = "cargoInfoDto", source = "cargo"),
    })
    MainRoutePointInfoDto toMainInfo(
            String valueFrom,
            String valueTo,
            Long distance,
            MainCargoInfoDto cargo
    );

    @Mappings({
            @Mapping(target = "city", source = "cityEntity"),
            @Mapping(target = "cargo.cargos", source = "cargoEntityList"),
            @Mapping(target = "id", ignore = true)
    })
    RoutePointEntity toEntity(
            RoutePointForOrderDto routePointForOrderDto,
            CityEntity cityEntity,
            List<CargoEntity> cargoEntityList
    );

    @Mappings({
            @Mapping(target = "cityName", source = "routePoint.city.name"),
            @Mapping(target = "operationType", source = "routePoint.operationType"),
            @Mapping(target = "cargoInfo", source = "cargoInfoDto"),
            @Mapping(target = "distance", source = "distance")
    })
    RoutePointInfoDto toInfoDto(
            RoutePointEntity routePoint,
            Set<CargoInfoDto> cargoInfoDto,
            Long distance
    );

    @Mappings({
            @Mapping(target = "cityName", source = "routePoint.city.name"),
            @Mapping(target = "operationType", source = "routePoint.operationType"),
            @Mapping(target = "cargoInfo", source = "cargoInfoDto"),
    })
    RoutePointInfoDto toInfoDto(
            RoutePointEntity routePoint,
            Set<CargoInfoDto> cargoInfoDto
    );


    RoutePoint toDomain(RoutePointDto routePoint);

    RoutePointDto toDto(RoutePoint routePoint);

    RoutePoint toDomain(RoutePointEntity entity);


//    BaseRoutePointEntity toBaseEntity(
//            List<BaseCargoEntity> cargoEntity,
//            String cityFrom,
//            String cityTo,
//            Long distance
//    );

}
