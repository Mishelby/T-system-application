package org.example.logisticapplication.domain.Admin;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseAdminDto(
        Long id,
        String name,
        String email,
        String message
) {
}
