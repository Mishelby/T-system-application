package org.example.logisticapplication.controllers.rest;

import jakarta.validation.constraints.NotNull;

public record UserParamDto(
        @NotNull
        String userName,
        @NotNull
        Long userNumber
) {
}
