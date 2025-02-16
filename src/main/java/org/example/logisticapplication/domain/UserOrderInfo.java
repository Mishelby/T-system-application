package org.example.logisticapplication.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_order_info") // Исправил на корректное имя таблицы
@Getter
@Setter
public class UserOrderInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "city_from")
    private String cityFromName;

    @Column(name = "station_from")
    private String stationFromName;

    @Column(name = "city_to")
    private String cityToName;

    @Column(name = "station_to")
    private String stationToName;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "distance_info")
    private String distanceInfo;

    public UserOrderInfo(
            String orderNumber,
            String userName,
            String cityFromName,
            String stationFromName,
            String cityToName,
            String stationToName,
            Double weight,
            Double distance,
            String distanceInfo
    ) {
        this.orderNumber = orderNumber;
        this.userName = userName;
        this.cityFromName = cityFromName;
        this.stationFromName = stationFromName;
        this.cityToName = cityToName;
        this.stationToName = stationToName;
        this.weight = weight;
        this.distance = distance;
        this.distanceInfo = distanceInfo;
    }

    public UserOrderInfo() {
    }
}

