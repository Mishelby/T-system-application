package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.User.UserInfoDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderForSubmittingDto(
        UserInfoDto userInfoDto,
        List<RoutePointInfoDto> baseRoutePoints
) {
}
