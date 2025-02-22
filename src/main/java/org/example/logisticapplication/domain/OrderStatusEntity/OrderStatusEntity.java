package org.example.logisticapplication.domain.OrderStatusEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Order.OrderStatus;

@Entity
@Table(name = "order_status")
@Getter
@Setter
@EqualsAndHashCode(of = "orderStatus")
public class OrderStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    public OrderStatusEntity(
            OrderStatus status
    ) {
        this.status = status;
    }

    public OrderStatusEntity() {}

    public String getStatusName(){
        return status.name();
    }

}
