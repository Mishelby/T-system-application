package org.example.logisticapplication.controllers.mvc;

import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.User.UserInfoDto;

import java.util.List;

public record OrderForSubmittingDto(
        UserInfoDto userInfoDto,
        List<RoutePointInfoDto> baseRoutePoints
) {
}
