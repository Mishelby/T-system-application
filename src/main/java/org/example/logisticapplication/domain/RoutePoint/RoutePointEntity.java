package org.example.logisticapplication.domain.RoutePoint;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;

import java.util.List;

@Entity
@Table(name = "route_point")
@Getter
@Setter
public class RoutePointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private CityEntity city;

    @Column(name = "operation_type")
    @Pattern(regexp = "LOADING|UNLOADING", message = "Invalid operation type")
    private String operationType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private CargoEntity cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    public RoutePointEntity(
            Long id,
            CityEntity city,
            String operationType,
            CargoEntity cargo
    ) {
        this.id = id;
        this.city = city;
        this.operationType = operationType;
        this.cargo = cargo;
    }

    public RoutePointEntity() {
    }
}
