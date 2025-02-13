package org.example.logisticapplication.domain.OrderStatusEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_status")
@Getter
@Setter
@EqualsAndHashCode(of = "orderStatus")
public class OrderStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "COMPLETED|NOT_COMPLETED", message = "Invalid driver status")
    @Column(name = "status")
    private String statusName;

    public OrderStatusEntity(String statusName) {
        this.statusName = statusName;
    }

    public OrderStatusEntity() {}

}
