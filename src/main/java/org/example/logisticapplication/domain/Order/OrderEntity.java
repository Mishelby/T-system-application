package org.example.logisticapplication.domain.Order;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.OrderCargo.OrderCargo;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_number", nullable = false, unique = true)
    private String uniqueNumber;

    @Column(name = "status", nullable = false)
    @Pattern(regexp = "COMPLETED|NOT COMPLETED", message = "Invalid order status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_map_id")
    private CountryMapEntity countryMap;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RoutePointEntity> routePoints;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<DriverOrderEntity> driverOrders;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<TruckOrderEntity> truckOrders;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderCargo> orderCargo;

    public OrderEntity(
            String uniqueNumber,
            CountryMapEntity countryMap,
            Set<RoutePointEntity> routePoints,
            Set<DriverOrderEntity> driverOrders,
            Set<TruckOrderEntity> truckOrders,
            List<OrderCargo> cargo
    ) {
        this.uniqueNumber = uniqueNumber;
        this.countryMap = countryMap;
        this.routePoints = routePoints;
        this.driverOrders = driverOrders;
        this.truckOrders = truckOrders;
        this.orderCargo = cargo;
    }

    public OrderEntity(
            String uniqueNumber,
            String status,
            CountryMapEntity countryMap
    ) {
        this.uniqueNumber = uniqueNumber;
        this.status = status;
        this.countryMap = countryMap;
    }

    public OrderEntity() {}
}
