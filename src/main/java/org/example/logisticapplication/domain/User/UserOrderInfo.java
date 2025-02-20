package org.example.logisticapplication.domain.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.logisticapplication.domain.Driver.DriverInfoForUserDto;

@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_EMPTY)
public record UserOrderInfo(
        String uniqueNumber,
        String status,
        String message,
        @JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_EMPTY)
        DriverInfoForUserDto orderInfoDto
) {
}
