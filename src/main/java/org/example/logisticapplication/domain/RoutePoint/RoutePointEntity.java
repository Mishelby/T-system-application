package org.example.logisticapplication.domain.RoutePoint;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;

import java.util.List;

@Entity
@Table(name = "route_points")
@Getter
@Setter
public class RoutePointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "operation_type")
    private String operationType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private CargoEntity cargo;

    public RoutePointEntity(
            Long id,
            String city,
            String operationType
    ) {
        this.id = id;
        this.city = city;
        this.operationType = operationType;
    }

    public RoutePointEntity() {
    }

}
