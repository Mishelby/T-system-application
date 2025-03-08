package org.example.logisticapplication.domain.OrderDistanceEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.CityStationDistance.CityStationDistanceEntity;
import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;

@Entity
@Table(name = "order_distance")
@Getter
@Setter
public class OrderDistanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_distance_id", nullable = false)
    private CityStationDistanceEntity distance;

    public OrderDistanceEntity(
            OrderEntity order,
            CityStationDistanceEntity distance
    ) {
        this.order = order;
        this.distance = distance;
    }

    public OrderDistanceEntity() {

    }
}
