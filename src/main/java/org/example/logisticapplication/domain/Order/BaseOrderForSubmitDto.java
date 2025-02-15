package org.example.logisticapplication.domain.Order;

import org.example.logisticapplication.domain.Distance.DistanceInfoDto;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoForUserDto;
import org.example.logisticapplication.domain.User.UserInfoDto;

public record BaseOrderForSubmitDto(
        String uniqueNumber,
        String orderStatus,
        String countyMapName,
        UserInfoDto userInfoDto,
        RoutePointInfoForUserDto routePointDto,
        DistanceInfoDto distanceInfoDto
) {
}
