package org.example.logisticapplication.domain.RoutePoint;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "route_point")
@Getter
@Setter
public class RoutePointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private CityEntity city;

//    @Version
//    private Long version;

    @Column(name = "operation_type")
    @Pattern(regexp = "LOADING|UNLOADING", message = "Invalid operation type")
    private String operationType;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "route_point_cargo",
            joinColumns = @JoinColumn(name = "route_point_id"),
            inverseJoinColumns = @JoinColumn(name = "cargo_id")
    )
    private List<CargoEntity> cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    public RoutePointEntity(
            Long id,
            CityEntity city,
            String operationType,
            List<CargoEntity> cargo
    ) {
        this.id = id;
        this.city = city;
        this.operationType = operationType;
        this.cargo = cargo;
    }

    public RoutePointEntity() {
    }

}
