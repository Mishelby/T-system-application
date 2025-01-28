package org.example.logisticapplication.domain.RoutePoint;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


//@Entity
//@Table(name = "base_route_point")
//@Getter
//@Setter
//public class BaseRoutePointEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "cityFrom", nullable = false, length = 30)
//    private String cityFrom;
//
//    @Column(name = "cityTo", nullable = false, length = 30)
//    private String cityTo;
//
//    @ManyToOne
//    @JoinColumn(name = "base_cargo_id", nullable = false)
//    private BaseCargoEntity baseCargo;
//
//    @Column(nullable = false)
//    private Integer distance;
//
//    public BaseRoutePointEntity(
//            String cityFrom,
//            String cityTo,
//            BaseCargoEntity baseCargo,
//            Integer distance
//    ) {
//        this.cityFrom = cityFrom;
//        this.cityTo = cityTo;
//        this.baseCargo = baseCargo;
//        this.distance = distance;
//    }
//
//    public BaseRoutePointEntity() {}
//}

