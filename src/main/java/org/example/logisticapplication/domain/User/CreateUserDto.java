package org.example.logisticapplication.domain.User;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateUserDto(
        Long id,
        String username,
        String password,
        String email
) {
}
