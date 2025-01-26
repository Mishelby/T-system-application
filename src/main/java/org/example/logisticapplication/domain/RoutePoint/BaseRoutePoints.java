package org.example.logisticapplication.domain.RoutePoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.logisticapplication.domain.Cargo.BaseCargoDto;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@AllArgsConstructor
public class BaseRoutePoints {
    String cityFrom;
    String cityTo;
    String operationType;
    List<BaseCargoDto> cargoDtoList;
    Long distance;
}
