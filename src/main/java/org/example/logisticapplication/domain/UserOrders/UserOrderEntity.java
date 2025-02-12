package org.example.logisticapplication.domain.UserOrders;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.User.UserEntity;

@Entity
@Table(name = "user_orders")
@Getter
@Setter
public class UserOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    public UserOrderEntity(
            UserEntity user,
            OrderEntity order
    ) {
        this.user = user;
        this.order = order;
    }

    public UserOrderEntity() {}
}

