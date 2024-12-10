package org.example.logisticapplication.domain.CountryMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CountryMapDto(
        @Null
        Long id,
        String name,
        List<Long> citiesId,
        List<Long> distancesId
) {
}
