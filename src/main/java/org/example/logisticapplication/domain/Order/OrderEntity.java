package org.example.logisticapplication.domain.Order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;

import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_number")
    private String uniqueNumber;

    @Column(name = "order_status")
    private String orderStatus;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "orders_route_points",
            joinColumns = @JoinColumn(
                    name = "order_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "route_point_id"
            )
    )
    private List<RoutePointEntity> routePoints;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "driver_orders",
            joinColumns = @JoinColumn(
                    name = "order_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "driver_id"
            )
    )
    public List<DriverEntity> drivers;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "truck_id")
    public TruckEntity truck;

    public OrderEntity(
            Long id,
            String uniqueNumber,
            String orderStatus,
            List<RoutePointEntity> routePoints,
            List<DriverEntity> drivers,
            TruckEntity truck
    ) {
        this.id = id;
        this.uniqueNumber = uniqueNumber;
        this.orderStatus = orderStatus;
        this.routePoints = routePoints;
        this.drivers = drivers;
        this.truck = truck;
    }

    public OrderEntity() {}
}
