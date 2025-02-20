package org.example.logisticapplication.domain.Admin;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateAdminDto(
        String username,
        String password,
        String email,
        String name
) {
}
