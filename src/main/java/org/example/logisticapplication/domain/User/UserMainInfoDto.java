package org.example.logisticapplication.domain.User;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserMainInfoDto(
        Long id,
        String name,
        String email,
        String createdAt
) {
}
