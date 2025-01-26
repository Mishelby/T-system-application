package org.example.logisticapplication.domain.RoutePoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.logisticapplication.domain.Cargo.CargoInfoDto;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@AllArgsConstructor
public class RoutePointInfoDto{
    String cityName;
    Set<CargoInfoDto> cargoInfo;
    String operationType;
    Long distance;
}
