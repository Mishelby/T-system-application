package org.example.logisticapplication.domain.RoutePoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.logisticapplication.domain.Cargo.BaseCargoDto;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@AllArgsConstructor
public class BaseRoutePoints {
    String cityFrom;
    String cityTo;
    BaseCargoDto cargoDto;
    Long distance;
}
