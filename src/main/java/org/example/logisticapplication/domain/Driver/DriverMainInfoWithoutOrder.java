package org.example.logisticapplication.domain.Driver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;


@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@AllArgsConstructor
public class DriverMainInfoWithoutOrder implements DriverInfo {
    String name;
    String phone;
    String message;

    @Override
    public boolean getCurrentOrder() {
        return false;
    }
}
