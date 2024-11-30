package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Order.OrderDto;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderStatus", defaultValue = "NOT_COMPLETE")
    @Mapping(target = "routePoints", expression = "java(emptyRoutePoints())")
    OrderEntity toEntity(Order order);

    Order toDomain(OrderDto orderDto);

    OrderDto toDto(Order order);

    Order toDomain(OrderEntity entity);

    default List<Long> mapRoutePointsToIds(List<RoutePointEntity> routePoints) {
        if(routePoints == null) {
            return Collections.emptyList();
        }

        return routePoints
                .stream()
                .map(RoutePointEntity::getId)
                .collect(Collectors
                        .toList());
    }

    default List<RoutePointEntity> emptyRoutePoints() {
        return Collections.emptyList();
    }

}
