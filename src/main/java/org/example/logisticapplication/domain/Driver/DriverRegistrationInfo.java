package org.example.logisticapplication.domain.Driver;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DriverRegInfoDto.class, name = "driverRegistrationDto")
})
public interface DriverRegistrationInfo {
    String getName();
    String getSecondName();
    String getUserName();
    String getPassword();
    String getEmail();
    Long getPersonNumber();
    String getCityName();
}
