package org.example.logisticapplication.domain.City;

import jakarta.validation.constraints.Null;

import java.util.List;

public record City(
        Long id,

        String name,

        Long countyMapId
) {
}
