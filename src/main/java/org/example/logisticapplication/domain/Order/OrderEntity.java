package org.example.logisticapplication.domain.Order;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;

import java.util.List;

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
    @JoinColumn(name = "country_map_id", nullable = false)
    private CountryMapEntity countryMap;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutePointEntity> routePoints;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DriverOrderEntity> driverOrders;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TruckOrderEntity> truckOrders;

    public OrderEntity(
            String uniqueNumber,
            String status,
            CountryMapEntity countryMap,
            List<RoutePointEntity> routePoints,
            List<DriverOrderEntity> driverOrders,
            List<TruckOrderEntity> truckOrders
    ) {
        this.uniqueNumber = uniqueNumber;
        this.status = status;
        this.countryMap = countryMap;
        this.routePoints = routePoints;
        this.driverOrders = driverOrders;
        this.truckOrders = truckOrders;
    }

    public OrderEntity() {}
}
