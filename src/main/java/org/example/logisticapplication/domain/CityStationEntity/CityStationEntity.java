package org.example.logisticapplication.domain.CityStationEntity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;

@Entity
@Table(name = "city_station")
@Getter
@Setter
@EqualsAndHashCode(exclude  = "city")
public class CityStationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private CityEntity city;

    public CityStationEntity(
            String name,
            CityEntity city
    ) {
        this.name = name;
        this.city = city;
    }

    public CityStationEntity() {}

}
