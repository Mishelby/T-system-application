package org.example.logisticapplication.domain.CountryMap;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CountryMap(
        String name
) {
}
