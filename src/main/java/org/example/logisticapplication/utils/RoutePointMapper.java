package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.RoutePoint.RoutePoint;
import org.example.logisticapplication.domain.RoutePoint.RoutePointDto;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoutePointMapper {

    RoutePoint toDomain(RoutePointDto routePoint);

    RoutePointDto toDto(RoutePoint routePoint);

    RoutePointEntity toEntity(RoutePoint routePoint);

    RoutePoint toDomain(RoutePointEntity entity);


}
