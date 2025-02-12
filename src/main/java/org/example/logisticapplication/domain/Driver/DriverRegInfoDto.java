package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("driverRegistrationDto")
public record DriverRegInfoDto(
        String userName,
        String password,
        String email,
        String name,
        String secondName,
        Long personNumber,
        String currentCityName
) implements DriverRegistrationInfo {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSecondName() {
        return secondName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Long getPersonNumber() {
        return personNumber;
    }

    @Override
    public String getCityName() {
        return currentCityName;
    }
}
