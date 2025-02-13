package org.example.logisticapplication.domain.DriverOrderEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;

@Entity
@Table(name = "driver_order")
@Getter
@Setter
public class DriverOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverEntity driver;

    public DriverOrderEntity(
            OrderEntity order,
            DriverEntity driver
    ) {
        this.order = order;
        this.driver = driver;
    }

    public DriverOrderEntity() {}
}

