package org.example.logisticapplication.domain.OrderTimeEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "order_time")
@Getter
@Setter
public class OrderTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "time_without_drivers")
    private Double timeWithoutDrivers;

    @Column(name = "truck_id")
    private Long trucksId;

    public OrderTimeEntity(
            String orderNumber,
            Double timeWithoutDrivers,
            Long trucksId
    ) {
        this.orderNumber = orderNumber;
        this.timeWithoutDrivers = timeWithoutDrivers;
        this.trucksId = trucksId;
    }

    public OrderTimeEntity() {}
}
