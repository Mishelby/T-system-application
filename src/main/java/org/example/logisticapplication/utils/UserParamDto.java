package org.example.logisticapplication.utils;

import jakarta.validation.constraints.NotNull;

public record UserParamDto(
        @NotNull
        String userName,
        @NotNull
        Long userNumber
) {
}
