package org.example.logisticapplication.domain.CountryMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CountryMapDto(
        @Null
        Long id,
        String name,
        Set<Long> citiesId,
        Set<Long> distancesId
) {
}
