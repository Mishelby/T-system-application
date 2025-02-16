package org.example.logisticapplication.domain.City;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record City(
        Long id,
        String name,
        String countryMapName
) {
}
