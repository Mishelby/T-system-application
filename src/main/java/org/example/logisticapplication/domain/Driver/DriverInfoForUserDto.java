package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DriverInfoForUserDto(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Map<List<String>, List<Long>> driversInfo,
        String orderStartDate
) {
}
