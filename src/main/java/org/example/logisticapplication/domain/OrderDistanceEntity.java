package org.example.logisticapplication.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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
    @JoinColumn(name = "distance_id", nullable = false)
    private DistanceEntity distance;

    public OrderDistanceEntity(
            OrderEntity order,
            DistanceEntity distance
    ) {
        this.order = order;
        this.distance = distance;
    }

    public OrderDistanceEntity() {

    }
}
