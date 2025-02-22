package org.example.logisticapplication.domain.OrderInfo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;

@Entity
@Table(name = "order_info")
@Getter
@Setter
public class OrderInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_from_id")
    private CityEntity cityFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_station_from_id")
    private CityStationEntity cityStationFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_to_id")
    private CityEntity cityTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_station_to_id")
    private CityStationEntity cityStationTo;

    public OrderInfoEntity(
            OrderEntity order,
            CityEntity cityFrom,
            CityStationEntity cityStationFrom,
            CityEntity cityTo,
            CityStationEntity cityStationTo
    ) {
        this.order = order;
        this.cityFrom = cityFrom;
        this.cityStationFrom = cityStationFrom;
        this.cityTo = cityTo;
        this.cityStationTo = cityStationTo;
    }

    public OrderInfoEntity() {
    }
}
