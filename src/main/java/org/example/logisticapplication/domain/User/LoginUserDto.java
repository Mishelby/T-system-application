package org.example.logisticapplication.domain.User;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginUserDto(
        String username,
        String password
) {
}
