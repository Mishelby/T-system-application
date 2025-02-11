package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverLoginInfo(
        String name,
        String password,
        Long personNumber
) {
}
