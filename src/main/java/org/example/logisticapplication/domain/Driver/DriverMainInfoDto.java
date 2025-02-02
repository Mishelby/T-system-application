package org.example.logisticapplication.domain.Driver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.logisticapplication.domain.Order.OrderMainInfo;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@AllArgsConstructor
public class DriverMainInfoDto implements DriverInfo{
    Long id;
    String name;
    String phone;
    OrderMainInfo orderInfoDto;

    @Override
    public boolean getCurrentOrder() {
        return orderInfoDto != null;
    }
}
