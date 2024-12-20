package org.example.logisticapplication.domain.CountryMap;

import jakarta.validation.constraints.Null;

import java.util.List;

public record CountryMap(
        Long id,
        String name,
        List<Long> citiesId,
        List<Long> distancesId
) {
}
