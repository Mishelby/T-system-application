package org.example.logisticapplication.domain.OrderCargo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;

@Entity
@Table(name = "order_cargo")
@Getter
@Setter
public class OrderCargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_number", referencedColumnName = "unique_number", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    private CargoEntity cargo;

    public OrderCargo(
            OrderEntity order,
            CargoEntity cargo
    ) {
        this.order = order;
        this.cargo = cargo;
    }

    public OrderCargo() {
    }
}
