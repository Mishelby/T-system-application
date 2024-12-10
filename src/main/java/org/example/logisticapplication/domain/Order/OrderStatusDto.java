package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)// to ignore null values
public record OrderStatusDto(
        String uniqueNumber,
        String status
) {
}
