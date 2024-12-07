package org.example.logisticapplication.domain.TruckOrderEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;

@Entity
@Table(name = "truck_order")
@Getter
@Setter
public class TruckOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "truck_id", nullable = false)
    private TruckEntity truck;

    public TruckOrderEntity(
            OrderEntity order,
            TruckEntity truck
    ) {
        this.order = order;
        this.truck = truck;
    }

    public TruckOrderEntity() {}
}
