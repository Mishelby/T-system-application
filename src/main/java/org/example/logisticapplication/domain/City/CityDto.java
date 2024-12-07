package org.example.logisticapplication.domain.City;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CityDto(
        @Null
        Long id,

        String name,

        Long countyMapId
) {
}
