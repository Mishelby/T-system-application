package org.example.logisticapplication.domain.User;

import jakarta.validation.constraints.Null;

public record CreateUserDto(
        @Null
        Long id,
        String username,
        String password,
        String email
) {
}
