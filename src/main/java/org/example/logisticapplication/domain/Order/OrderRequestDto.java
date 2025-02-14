package org.example.logisticapplication.domain.Order;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderRequestDto(
        Long userId,
        String departureCity,
        String arrivalCity,
        Double cargoWeight,
        LocalDate departureDate,
        List<String> departureStations
) {
}
