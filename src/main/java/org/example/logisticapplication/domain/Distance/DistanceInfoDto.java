package org.example.logisticapplication.domain.Distance;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DistanceInfoDto(
        String message
) implements DistanceInfo {
}

