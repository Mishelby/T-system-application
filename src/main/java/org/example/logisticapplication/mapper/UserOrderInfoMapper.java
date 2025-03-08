package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Order.OrderInfoForUserDto;
import org.example.logisticapplication.domain.UserOrderInfo.UserOrderInfoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserOrderInfoMapper {

    @Mappings({
            @Mapping(target = "orderNumber", source = "uniqueNumber"),
            @Mapping(target = "userName", source = "userName"),
            @Mapping(target = "cityFromName", source = "orderInfo.routePointInfo.cityFrom"),
            @Mapping(target = "stationFromName", source = "orderInfo.routePointInfo.stationFrom"),
            @Mapping(target = "cityToName", source = "orderInfo.routePointInfo.cityTo"),
            @Mapping(target = "stationToName", source = "orderInfo.routePointInfo.stationTo"),
            @Mapping(target = "weight", source = "orderInfo.routePointInfo.weight"),
            @Mapping(target = "distance", source = "orderInfo.routePointInfo.distance"),
            @Mapping(target = "distanceInfo", source = "orderInfo.distanceInfoDto.message"),
            @Mapping(target = "desiredDate", source = "orderInfo.userInfoDto.desiredDate"),
    })
    UserOrderInfoEntity toEntity(
            final String uniqueNumber,
            final String userName,
            final OrderInfoForUserDto orderInfo
    );
}
