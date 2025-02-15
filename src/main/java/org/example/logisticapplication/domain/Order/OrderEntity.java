package org.example.logisticapplication.domain.Order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.OrderCargo.OrderCargoEntity;
import org.example.logisticapplication.domain.OrderStatusEntity.OrderStatusEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;

import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private OrderStatusEntity status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "country_map_id")
    private CountryMapEntity countryMap;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RoutePointEntity> routePoints;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<DriverOrderEntity> driverOrders;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<TruckOrderEntity> truckOrders;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderCargoEntity> orderCargoEntities;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    public OrderEntity(
            String uniqueNumber,
            CountryMapEntity countryMap,
            Set<RoutePointEntity> routePoints,
            Set<DriverOrderEntity> driverOrders,
            Set<TruckOrderEntity> truckOrders,
            List<OrderCargoEntity> cargo
    ) {
        this.uniqueNumber = uniqueNumber;
        this.countryMap = countryMap;
        this.routePoints = routePoints;
        this.driverOrders = driverOrders;
        this.truckOrders = truckOrders;
        this.orderCargoEntities = cargo;
    }

    public OrderEntity(
            String uniqueNumber,
            OrderStatusEntity status,
            CountryMapEntity countryMap
    ) {
        this.uniqueNumber = uniqueNumber;
        this.status = status;
        this.countryMap = countryMap;
    }

    public OrderEntity(
            String uniqueNumber,
            CountryMapEntity countryMap
    ) {
        this.uniqueNumber = uniqueNumber;
        this.countryMap = countryMap;
    }

    public OrderEntity() {}

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
    }
}
