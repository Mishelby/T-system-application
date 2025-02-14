package org.example.logisticapplication.domain.RoutePoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.logisticapplication.domain.Cargo.CargoInfoDto;

import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@AllArgsConstructor
public class RoutePointInfoDto{
    String cityName;
    String stationName;
    List<CargoInfoDto> cargoInfo;
    String operationType;
    Double distance;
}
