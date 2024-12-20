package org.example.logisticapplication.domain.CountryMap;

import jakarta.validation.constraints.Null;

import java.util.List;
import java.util.Set;

public record CountryMap(
        Long id,
        String name,
        Set<Long> citiesId,
        Set<Long> distancesId
) {
}
