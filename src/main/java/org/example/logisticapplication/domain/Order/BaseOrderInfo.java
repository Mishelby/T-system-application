package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.User.UserInfoDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseOrderInfo(
        String uniqueNumber,
        UserInfoDto userInfoDto
) {
}
