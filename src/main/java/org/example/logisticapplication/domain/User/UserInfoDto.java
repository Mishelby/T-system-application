package org.example.logisticapplication.domain.User;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserInfoDto(
        String name,
        String createdAt,
        String desiredDate
) {
}
